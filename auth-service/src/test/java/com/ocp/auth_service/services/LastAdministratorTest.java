package com.ocp.auth_service.services;
import com.ocp.auth_service.entity.*;
import com.ocp.auth_service.repository.*;
import com.ocp.auth_service.dto.request.*;
import com.ocp.auth_service.exception.UserDeletionNotAllowedException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class LastAdministratorTest {
 @Mock UserCredentialRepository userCredentialRepository; @Mock RoleRepository roleRepository; @InjectMocks UserService service;
 UserCredential admin;
 @BeforeEach void setup(){Role role=new Role();role.setName("ADMIN");role.setId(UUID.randomUUID());admin=UserCredential.builder().id(UUID.randomUUID()).userId(UUID.randomUUID()).enabled(true).accountLocked(false).role(role).build();when(userCredentialRepository.lockAdministrators()).thenReturn(List.of(admin));when(userCredentialRepository.findByUserId(admin.getUserId())).thenReturn(Optional.of(admin));}
 @Test void cannotDisableLastAdministrator(){var r=new UpdateUserRequest();r.setEnabled(false);assertThrows(UserDeletionNotAllowedException.class,()->service.updateUser(admin.getUserId(),r));verify(userCredentialRepository,never()).save(any());}
 @Test void cannotLockLastAdministrator(){var r=new UpdateUserRequest();r.setAccountLocked(true);assertThrows(UserDeletionNotAllowedException.class,()->service.updateUser(admin.getUserId(),r));}
 @Test void cannotDemoteLastAdministrator(){Role employee=new Role();employee.setId(UUID.randomUUID());employee.setName("EMPLOYEE");when(roleRepository.findById(employee.getId())).thenReturn(Optional.of(employee));var r=new AssignRoleRequest();r.setUserId(admin.getUserId());r.setRoleId(employee.getId());assertThrows(UserDeletionNotAllowedException.class,()->service.assignRole(r));}
}
