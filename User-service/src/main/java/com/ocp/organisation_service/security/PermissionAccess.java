package com.ocp.organisation_service.security;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.core.Authentication;
import java.util.function.Supplier;
import java.util.Set;
public final class PermissionAccess implements AuthorizationManager<RequestAuthorizationContext> {
 public AuthorizationDecision check(Supplier<Authentication> supplier,RequestAuthorizationContext context){
  Authentication auth=supplier.get();if(auth==null||!auth.isAuthenticated())return new AuthorizationDecision(false);
  var authorities=auth.getAuthorities().stream().map(a->a.getAuthority().toUpperCase()).toList();
  if(authorities.stream().anyMatch(a->Set.of("ROLE_ADMIN","ROLE_ADMINISTRATOR","ROLE_ADMINISTRATEUR").contains(a)))return new AuthorizationDecision(true);
  String path=context.getRequest().getRequestURI(),method=context.getRequest().getMethod();
  String resource=path.contains("/teams")?"TEAM":path.contains("/departements")?"DEPARTMENT":path.contains("/permissions")?"PERMISSION":path.contains("/roles")?"ROLE":"USER";
  String action=switch(method){case "GET"->"READ";case "POST"->"CREATE";case "DELETE"->"DELETE";default->"UPDATE";};
  if(path.contains("assign-roles"))action="UPDATE";
  String required=resource+"_"+action;
  return new AuthorizationDecision(authorities.contains(required));
 }
}
