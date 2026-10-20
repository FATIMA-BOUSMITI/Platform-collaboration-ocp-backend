package com.ocp.auth_service.services;
import com.ocp.auth_service.entity.*;
import com.ocp.auth_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;
@Service @RequiredArgsConstructor
public class AdminService {
 private final RefreshTokenRepository tokens;
 private final LoginHistoryRepository history;
 private final UserCredentialRepository users;
 private final AdminPreferenceRepository preferences;
 private final AdminNoticeRepository notices;
 public record SessionView(UUID id, String email, UUID userId, LocalDateTime createdAt, LocalDateTime expiresAt, boolean active) {}
 public record Activity(UUID id, String email, LocalDateTime date, boolean success, String device, String ipAddress) {}
 @Transactional(readOnly=true)
 public List<SessionView> sessions(String email, boolean admin) {
  return (admin ? tokens.findAll() : tokens.findByUser(user(email))).stream()
   .sorted(Comparator.comparing(RefreshToken::getCreatedAt).reversed())
   .map(t -> new SessionView(t.getId(),t.getUser().getEmail(),t.getUser().getUserId(),t.getCreatedAt(),t.getExpiryDate(),!t.isRevoked() && t.getExpiryDate().isAfter(LocalDateTime.now()))).toList();
 }
 @Transactional
 public void revoke(UUID id,String email,boolean admin) {
  RefreshToken t=tokens.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Session introuvable"));
  if(!admin && !t.getUser().getEmail().equalsIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  t.setRevoked(true);tokens.save(t);
 }
 @Transactional(readOnly=true)
 public List<Activity> activity() {return history.findTop50ByOrderByLoginDateDesc().stream().map(h -> new Activity(h.getId(),h.getUser().getEmail(),h.getLoginDate(),Boolean.TRUE.equals(h.getSuccess()),h.getDevice(),h.getIpAddress())).toList();}
 @Transactional(readOnly=true)
 public Map<String,Long> stats() {return Map.of("totalUsers",users.count(),"activeUsers",users.countByEnabledTrue(),"lockedAccounts",users.countByAccountLockedTrue(),"totalFailedAttempts",history.countBySuccessFalseAndLoginDateBetween(LocalDateTime.now().minusHours(24),LocalDateTime.now()),"activeSessions",tokens.countByRevokedFalseAndExpiryDateAfter(LocalDateTime.now()));}
 @Transactional(readOnly=true)
 public Map<String,String> settings(String email) {return preferences.findById(user(email).getId()).map(p -> Map.of("language",p.getLanguage(),"timezone",p.getTimezone())).orElse(Map.of("language","fr","timezone","UTC"));}
 @Transactional
 public Map<String,String> settings(String email,Map<String,String> value) {
  String language=value.getOrDefault("language","fr"), timezone=value.getOrDefault("timezone","UTC");
  if(!Set.of("fr","en").contains(language)) throw new IllegalArgumentException("Langue invalide");
  try {java.time.ZoneId.of(timezone);} catch(Exception e){throw new IllegalArgumentException("Fuseau horaire invalide");}
  AdminPreference p=preferences.findById(user(email).getId()).orElseGet(AdminPreference::new);
  p.setId(user(email).getId());p.setLanguage(language);p.setTimezone(timezone);preferences.save(p);return settings(email);
 }
 public record NoticeView(UUID id, String message, LocalDateTime createdAt, boolean read) {}
 @Transactional(readOnly=true)
 public List<NoticeView> notifications(String email) {return notices.findByOwnerOrderByCreatedAtDesc(user(email).getId()).stream().map(n -> new NoticeView(n.getId(),n.getMessage(),n.getCreatedAt(),n.isRead())).toList();}
 @Transactional
 public void read(UUID id,String email) {AdminNotice n=notices.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));if(!n.getOwner().equals(user(email).getId()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);n.setRead(true);notices.save(n);}
 private UserCredential user(String email){return users.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));}
}
