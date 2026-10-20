# Plateforme collaborative OCP — Backend

Backend de la plateforme collaborative OCP, organisé en microservices et exposé au frontend via une API Gateway.

## Services locaux

| Service | Port | Rôle |
| --- | ---: | --- |
| API Gateway | 9000 | Point d'entrée HTTP et CORS |
| Auth service | 8080 | Authentification, rôles et permissions |
| User service | 8082 | Utilisateurs, départements et équipes |
| Project management | 8083 | Projets, tâches, sous-tâches et calendrier |
| Communication | 8089 | Conversations, messages et temps réel |

## Configuration locale

Les secrets et paramètres locaux doivent être placés dans `.env.local`, qui est ignoré par Git. Ne commitez jamais de mot de passe, secret JWT, jeton ou URL de base de données privée.

Variables utilisées localement :

```properties
JWT_SECRET=replace-with-a-long-local-secret
SPRING_DATASOURCE_URL=jdbc:postgresql://host/database
SPRING_DATASOURCE_USERNAME=local-user
SPRING_DATASOURCE_PASSWORD=local-password
SPRING_MAIL_HOST=smtp.example.test
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=local-mail-user
SPRING_MAIL_PASSWORD=local-mail-password
```

Chaque service qui importe ce fichier le charge de façon optionnelle. Pour un environnement partagé, fournissez les mêmes valeurs par variables d'environnement ou par un gestionnaire de secrets.

## Démarrage et validation

Depuis le dossier d'un service :

```bash
mvn spring-boot:run
mvn test
```

Démarrez les services dépendants avant le service concerné, puis la Gateway. Le frontend utilise ensuite `http://localhost:9000/api` par défaut.

## Routes ajoutées à la Gateway

La Gateway redirige les routes projets, tâches, sous-tâches et calendrier vers le service `project-management-service` sur le port 8083. Les routes permissions sont redirigées vers le service d'authentification sur le port 8080.

## Règles Git

- Ne versionnez pas `.env.local`, `target/`, `.idea/` ou des fichiers `.class`.
- Exécutez `mvn test` dans le module modifié avant un push.
- Gardez les commits ciblés et décrits clairement.
