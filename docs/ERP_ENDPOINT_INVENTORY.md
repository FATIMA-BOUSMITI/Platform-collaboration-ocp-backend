# Inventaire statique des contrôleurs existants

Annotations relevées dans les sources Java (hors tests), sans requête API. Le fichier source reste la référence pour les préfixes, permissions et DTO.

## auth-service/src/main/java/com/ocp/auth_service/controllers/AdminController.java

- REQUEST "/api/auth/admin"
- GET "/stats"
- GET "/activity"
- GET "/sessions"
- DELETE "/sessions/{id}"
- GET "/settings"
- PUT "/settings"
- GET "/notifications"
- PATCH "/notifications/{id}/read"

## auth-service/src/main/java/com/ocp/auth_service/controllers/AuthenticationController.java

- REQUEST "/api/auth"
- GET "/access-context"
- GET "/me"
- POST "/login"
- POST "/logout"
- POST "/refresh-token"
- POST "/forgot-password"
- POST "/reset-password"
- POST "/change-password/{credentialId}"

## auth-service/src/main/java/com/ocp/auth_service/controllers/PermissionController.java

- REQUEST "/api/permissions"
- POST (racine)
- GET (racine)
- GET "/{id}"

## auth-service/src/main/java/com/ocp/auth_service/controllers/RoleController.java

- REQUEST "/api/roles"
- PUT "/permissions/batch"
- PUT "/{id}"
- DELETE "/{id}"
- POST (racine)
- GET "/{id}"
- GET (racine)
- GET "/stats"
- GET "{id}/permissions"
- PUT "{id}/permissions"

## auth-service/src/main/java/com/ocp/auth_service/controllers/UserController.java

- REQUEST "/api/auth/users"
- POST (racine)
- GET "/{id}"
- GET "/user/{userId}"
- GET (racine)
- GET "/role-assignments"
- PUT "/{id}"
- POST "/assign-roles"
- DELETE "/{id}"
- DELETE "/user/{userId}"
- GET "/stats"
- GET "/stats/failed-attempts/last-24h"
- GET "/{userId}/roles"

## User-service/src/main/java/com/ocp/organisation_service/controllers/DepartementController.java

- REQUEST "/api/users/departements"
- GET (racine)
- GET "/{id}"
- POST (racine)
- PUT "/{id}"
- DELETE "/{id}"

## User-service/src/main/java/com/ocp/organisation_service/controllers/DirectoryController.java

- REQUEST "/api/users/directory"
- GET "/departments"
- GET (racine)

## User-service/src/main/java/com/ocp/organisation_service/controllers/TeamController.java

- REQUEST "/api/users/teams"
- GET (racine)
- POST (racine)
- PUT "/{id}"
- DELETE "/{id}"
- PUT "/{id}/members/{userId}"
- DELETE "/{id}/members/{userId}"

## User-service/src/main/java/com/ocp/organisation_service/controllers/UserProfileController.java

- REQUEST "/api/users"
- POST (racine)
- GET (racine)
- GET "/{id}"
- GET "/auth/{authUserId}"
- PUT "/{id}"
- DELETE "/{id}"

## project-management-service/src/main/java/com/ocp/project_management_service/controller/BoardController.java

- REQUEST "/api"
- GET "/projects/{projectId}/columns"
- POST "/projects/{projectId}/columns"
- PUT "/projects/{projectId}/columns/{id}"
- DELETE "/projects/{projectId}/columns/{id}"
- PATCH "/tasks/{id}/move"

## project-management-service/src/main/java/com/ocp/project_management_service/controller/CalendarController.java

- REQUEST "/api"
- POST "/projects/{projectId}/calendar"
- GET "/projects/{projectId}/calendar"
- DELETE "/calendar/{eventId}"

## project-management-service/src/main/java/com/ocp/project_management_service/controller/ProjectController.java

- REQUEST "/api/projects"
- POST (racine)
- GET (racine)
- GET "/{id}"
- GET "/department/{deptId}"
- PUT "/{id}"
- PATCH "/{id}/status"
- POST "/{id}/archive"
- DELETE "/{id}"

## project-management-service/src/main/java/com/ocp/project_management_service/controller/TaskController.java

- REQUEST "/api"
- POST "/projects/{projectId}/tasks"
- GET "/projects/{projectId}/tasks"
- GET "/tasks/{id}"
- GET "/tasks/assignee/{userId}"
- PUT "/tasks/{id}"
- PATCH "/tasks/{id}/status"
- POST "/tasks/{taskId}/subtasks"
- GET "/tasks/{taskId}/subtasks"
- PATCH "/subtasks/{id}/toggle"
- POST "/tasks/{taskId}/comments"
- GET "/tasks/{taskId}/comments"

## communication-service/src/main/java/com/ocp/communication_service/controller/AttachmentController.java

- REQUEST "/api/conversations/{conversationId}"
- POST value="/messages/{messageId}/attachments",consumes=MediaType.MULTIPART_FORM_DATA_VALUE
- GET "/attachments/{id}"

## communication-service/src/main/java/com/ocp/communication_service/controller/ConversationController.java

- REQUEST "/api/conversations"
- POST (racine)
- GET "/{id}"
- GET "/user/{userId}"
- POST "/{conversationId}/members/{userId}"

## communication-service/src/main/java/com/ocp/communication_service/controller/EventIngestionController.java

- REQUEST "/api/internal/collaboration/events"
- POST (racine)

## communication-service/src/main/java/com/ocp/communication_service/controller/MessageController.java

- REQUEST "/api/conversations/{conversationId}/messages"
- PATCH "/read"
- POST (racine)
- GET (racine)

## communication-service/src/main/java/com/ocp/communication_service/controller/NotificationController.java

- REQUEST "/api/notifications"
- GET (racine)
- GET "/unread-count"
- PATCH "/{notificationId}/read"
- PATCH "/read-all"
- GET "/preferences"
- PUT "/preferences"

