package com.ocp.auth_service.services;

import com.ocp.auth_service.dto.request.AssignRoleRequest;
import com.ocp.auth_service.dto.request.CreateUserRequest;
import com.ocp.auth_service.dto.request.UpdateUserRequest;
import com.ocp.auth_service.dto.response.UserResponse;
import com.ocp.auth_service.dto.response.UserRoleAssignmentResponse;
import com.ocp.auth_service.dto.response.UserStatsResponse;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.exception.EmailAlreadyExistsException;
import com.ocp.auth_service.exception.UserNotFoundException;
import com.ocp.auth_service.exception.UserDeletionNotAllowedException;
import com.ocp.auth_service.mappers.UserMapper;
import com.ocp.auth_service.repository.LoginHistoryRepository;
import com.ocp.auth_service.repository.PasswordResetTokenRepository;
import com.ocp.auth_service.repository.RefreshTokenRepository;
import com.ocp.auth_service.repository.RoleRepository;
import com.ocp.auth_service.repository.SessionRepository;
import com.ocp.auth_service.repository.UserCredentialRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
 private final com.ocp.auth_service.repository.AdminPreferenceRepository preferences;
 private final com.ocp.auth_service.repository.AdminNoticeRepository notices;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userCredentialRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
            .orElseThrow(() -> new RuntimeException("Role not found"));

        requireAdministratorFor(role);
        UserCredential user = UserCredential.builder()
            .userId(request.getUserId())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()+UUID.randomUUID()))
            .enabled(false)
            .accountLocked(true)
            .failedAttempts(0)
            .role(role)
            .build();

        UserCredential savedUser = userCredentialRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        UserCredential user = userCredentialRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUserId(UUID userId) {
        UserCredential user = userCredentialRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userCredentialRepository.findAll()
            .stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserRoleAssignmentResponse> getAllUserRoleAssignments() {
        return userCredentialRepository.findAllUserRoleAssignments();
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
 var administrators=userCredentialRepository.lockAdministrators();
        UserCredential user = userCredentialRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        requireAdministratorFor(user.getRole());
        if(request.getRoleId()!=null)requireAdministratorFor(roleRepository.findById(request.getRoleId()).orElseThrow());
        protectLastAdministrator(user,request.getEnabled(),request.getAccountLocked(),request.getRoleId(),administrators);
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        if (request.getAccountLocked() != null) {
            user.setAccountLocked(request.getAccountLocked());
            if(!request.getAccountLocked())user.setFailedAttempts(0);
        }

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }

        UserCredential updatedUser = userCredentialRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public UserResponse assignRole(AssignRoleRequest request) {
 var administrators=userCredentialRepository.lockAdministrators();
        UserCredential user = userCredentialRepository.findByUserId(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException(request.getUserId().toString()));

        Role role = roleRepository.findById(request.getRoleId())
            .orElseThrow(() -> new RuntimeException("Role not found"));

        requireAdministratorFor(user.getRole());requireAdministratorFor(role);
        protectLastAdministrator(user,null,null,role.getId(),administrators);
        user.setRole(role);
        UserCredential updatedUser = userCredentialRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        UserCredential user = userCredentialRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));
        deleteCredentialAndAssociations(user);
    }

    @Transactional
    public void deleteUserByUserId(UUID userId) {
        UserCredential user = userCredentialRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException(userId.toString()));
        deleteCredentialAndAssociations(user);
    }

    private void deleteCredentialAndAssociations(UserCredential user) {
        String roleName = user.getRole() == null ? "" : user.getRole().getName();
        if ("ADMIN".equalsIgnoreCase(roleName)
            || "ROLE_ADMIN".equalsIgnoreCase(roleName)
            || "ADMINISTRATOR".equalsIgnoreCase(roleName)
            || "ROLE_ADMINISTRATOR".equalsIgnoreCase(roleName)
            || "ADMINISTRATEUR".equalsIgnoreCase(roleName)
            || "ROLE_ADMINISTRATEUR".equalsIgnoreCase(roleName)) {
            LOGGER.warn(
                "Rejected deletion of protected administrator account credentialId={} userId={} role={}",
                user.getId(),
                user.getUserId(),
                roleName);
            throw new UserDeletionNotAllowedException(
                "Administrator accounts cannot be deleted through user management.");
        }

        LOGGER.info("Deleting authentication data for linked profile userId={}", user.getUserId());
        sessionRepository.deleteByUser(user);
        refreshTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.deleteByUser(user);
        loginHistoryRepository.deleteByUser(user);
        preferences.findById(user.getId()).ifPresent(preferences::delete);
 notices.deleteByOwner(user.getId());
 userCredentialRepository.delete(user);
    }



    private void requireAdministratorFor(Role role){
      var auth=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
      if(auth!=null && admin(role.getName()) && auth.getAuthorities().stream().noneMatch(a->java.util.Set.of("ROLE_ADMIN","ROLE_ADMINISTRATOR","ROLE_ADMINISTRATEUR").contains(a.getAuthority())))throw new org.springframework.security.access.AccessDeniedException("Seul un administrateur peut gérer un compte administrateur.");
    }
    private boolean admin(String name){return java.util.Set.of("ADMIN","ROLE_ADMIN","ADMINISTRATOR","ROLE_ADMINISTRATOR","ADMINISTRATEUR","ROLE_ADMINISTRATEUR").contains(name.toUpperCase());}
    private void protectLastAdministrator(UserCredential user,Boolean enabled,Boolean locked,UUID roleId,List<UserCredential> administrators){
      if(!admin(user.getRole().getName()) || !Boolean.TRUE.equals(user.getEnabled()) || Boolean.TRUE.equals(user.getAccountLocked()))return;
      boolean demote=roleId!=null && !admin(roleRepository.findById(roleId).orElseThrow().getName());
      if(Boolean.FALSE.equals(enabled)||Boolean.TRUE.equals(locked)||demote){
       long count=administrators.stream().filter(u->Boolean.TRUE.equals(u.getEnabled()) && !Boolean.TRUE.equals(u.getAccountLocked())).count();
       if(count<=1)throw new UserDeletionNotAllowedException("Le dernier administrateur actif doit conserver ses accès.");
      }
    }
    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats() {
        long totalUsers = userCredentialRepository.count();
        long activeUsers = userCredentialRepository.countByEnabledTrue();
        long lockedAccounts = userCredentialRepository.countByAccountLockedTrue();
        long totalFailedAttempts = userCredentialRepository.sumFailedAttempts();
        long usersWithFailedAttempts = userCredentialRepository.countByFailedAttemptsGreaterThan(0);

        return new UserStatsResponse(
            totalUsers,
            activeUsers,
            lockedAccounts,
            totalFailedAttempts,
            usersWithFailedAttempts
        );
    }

    @Transactional(readOnly = true)
    public long getFailedAttemptsLast24Hours() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusHours(24);

        return loginHistoryRepository.countBySuccessFalseAndLoginDateBetween(start, end);
    }
}
