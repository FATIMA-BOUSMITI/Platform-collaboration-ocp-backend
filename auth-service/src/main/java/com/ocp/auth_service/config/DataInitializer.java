package com.ocp.auth_service.config;

import com.ocp.auth_service.entity.Permission;
import com.ocp.auth_service.Repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {
        seedPermission("USER_CREATE", "Créer un utilisateur");
        seedPermission("USER_READ", "Consulter les utilisateurs");
        seedPermission("USER_UPDATE", "Modifier un utilisateur");
        seedPermission("USER_DELETE", "Supprimer un utilisateur");

        seedPermission("ROLE_CREATE", "Créer un rôle");
        seedPermission("ROLE_READ", "Consulter les rôles");
        seedPermission("ROLE_UPDATE", "Modifier un rôle");
        seedPermission("ROLE_DELETE", "Supprimer un rôle");

        seedPermission("PERMISSION_READ", "Consulter les permissions");
        seedPermission("PERMISSION_UPDATE", "Modifier les permissions");
    }

    private void seedPermission(String name, String description) {
        if (permissionRepository.existsByName(name)) {
            return;
        }
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        permissionRepository.save(permission);
    }
}
