package com.ocp.auth_service.config;
import com.ocp.auth_service.entity.Permission;
import com.ocp.auth_service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.transaction.annotation.Transactional;
@Component @RequiredArgsConstructor
public class AdminPermissionCatalogue implements ApplicationRunner {
 private final PermissionRepository permissions;
 @Override @Transactional public void run(ApplicationArguments args){
  var names=permissions.findAll().stream().map(Permission::getName).collect(java.util.stream.Collectors.toSet());
  for(String resource:java.util.List.of("USER","DEPARTMENT","TEAM","ROLE","PERMISSION"))for(String action:java.util.List.of("READ","CREATE","UPDATE","DELETE")){
   String name=resource+"_"+action;if(names.contains(name))continue;
   Permission p=new Permission();p.setName(name);p.setDescription(switch(action){case "READ"->"Consulter";case "CREATE"->"Créer";case "UPDATE"->"Modifier";default->"Supprimer";}+" · "+switch(resource){case "USER"->"utilisateurs";case "DEPARTMENT"->"départements";case "TEAM"->"équipes";case "ROLE"->"rôles";default->"permissions";});permissions.save(p);
  }
 }
}
