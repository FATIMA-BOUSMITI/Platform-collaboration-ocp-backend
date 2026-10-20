# Contrats API de collaboration

Inventaire issu du code, sans appels réseau de validation. Toutes les URLs REST ci-dessous sont préfixées par /api sur la gateway et par le client Axios. UUID sous forme de chaînes JSON, dates métier YYYY-MM-DD et timestamps ISO sans fuseau (modèle existant).

| Méthodes | URL | Usage et payload |
|---|---|---|
| GET | /users/directory | personnes {id,firstName,lastName,email} ; utilisateur authentifié |
| GET | /users/directory/departments | départements {id,name} ; utilisateur authentifié |
| GET, POST | /projects | liste autorisée / création : name,description,responsibleId,departmentId,startDate,endDate,projectPriority,budget |
| GET, PUT, DELETE | /projects/{id} | contrat projets existant, contrôle du projet côté serveur |
| GET | /projects/department/{departmentId} | projets du département accessibles |
| PATCH | /projects/{id}/status | objet {status}, conforme au client existant |
| POST | /projects/{id}/archive | archivage existant |
| GET, POST | /projects/{projectId}/columns | liste / {name,status,position?} |
| PUT, DELETE | /projects/{projectId}/columns/{id} | {name,status,position} / colonne vide uniquement |
| GET, POST | /projects/{projectId}/tasks | liste / {title,description,assigneeId?,priority,dueDate?,estimatedTimeMinutes?,columnId?} |
| GET, PUT | /tasks/{id} | lecture / mise à jour de tâche (titre et priorité requis) ; valeurs absentes d'affectation/date les effacent |
| GET | /tasks/assignee/{userId} | tâches affectées, filtrées selon les droits existants |
| PATCH | /tasks/{id}/move | {columnId,position,expectedVersion} ; réponse tâche avec position/version ; conflit 409 |
| PATCH | /tasks/{id}/status | {status} : TODO,IN_PROGRESS,REVIEW,DONE,ARCHIVED |
| GET, POST | /tasks/{id}/comments | lecture / {authorId,content} ; identité vérifiée côté serveur |
| GET, POST | /tasks/{id}/subtasks | lecture / {title} |
| PATCH | /subtasks/{id}/toggle | inversion de l'état de checklist |
| GET, POST | /projects/{id}/calendar | calendrier existant |
| DELETE | /calendar/{eventId} | suppression d'événement existante |
| POST | /conversations | {type,name?,creatorId,memberIds,projectId?} ; PRIVATE,CHANNEL,PROJECT (TEAM existant conservé) |
| GET | /conversations/user/{userId} | identité demandée = identité connectée |
| GET | /conversations/{id} | membre uniquement ; réponse id,type,name,projectId,createdAt,memberIds,adminIds |
| POST | /conversations/{id}/members/{userId} | administrateur du groupe ; interdit sur PRIVATE |
| GET, POST | /conversations/{id}/messages | historique / {senderId,content} ; membre et auteur authentifié |
| PATCH | /conversations/{id}/messages/read | aucune charge JSON ; mise à jour lastReadAt du membre |
| POST | /conversations/{id}/messages/{messageId}/attachments | multipart champ file, auteur uniquement, 10 Mo et cinq fichiers/message |
| GET | /conversations/{id}/attachments/{attachmentId} | membre uniquement ; téléchargement binaire authentifié |
| GET | /notifications?unread=false&page=0 | liste de 40 notifications de l'utilisateur connecté |
| GET | /notifications/unread-count | {count} |
| PATCH | /notifications/{id}/read | {read:boolean}, propriétaire uniquement |
| PATCH | /notifications/read-all | toutes les notifications de l'utilisateur connecté |
| GET, PUT | /notifications/preferences | {messages,tasks,deadlines} ; userId imposé côté serveur |

Réponse tâche enrichie : id,projectId,title,description,assigneeId,priority,status,dueDate,estimatedTimeMinutes,realTimeMinutes,comments,subTasks,columnId,position,version. Réponse message enrichie : champs existants + readBy (UUID des autres lecteurs) et attachments ({id,name,contentType,size}).

Le frontend garde responsableId/priority dans son formulaire projet historique, puis les traduit en responsibleId/projectPriority au point d'accès HTTP. Les statuts de tâche conservent REVIEW et ARCHIVED ; aucun regroupement silencieux en trois états.

WebSocket : /ws ; STOMP CONNECT Authorization Bearer ; SUBSCRIBE /user/queue/messages et /user/queue/notifications. Signaux messages : message,receipt,attachment avec conversationId. Signaux notifications : notification avec notification persistée, notification-state. L'événement connected est local au client et provoque une resynchronisation HTTP. Pas de commande STOMP SEND.

Endpoint interne hors gateway : POST /api/internal/collaboration/events, JWT de service signé ; {id,userId,type,title,body,link}. Liens autorisés /projects?... et /messages?... ; types MESSAGE,TASK_ASSIGNED,TASK_UPDATED,TASK_STATUS,TASK_COMMENT,TASK_DUE. Réponse HTTP de succès sans corps.

Erreurs : 400 payload invalide, 401 JWT/session invalide, 403 permission ou appartenance insuffisante, 404 ressource absente/non détenue, 409 version ou colonne incompatible, 413 limite multipart, 503 service dépendant indisponible. Les erreurs de connexion ou de droits d'une ressource ne doivent pas être interprétées comme un logout par le frontend.

## Compléments de la phase 5

Consulter [ERP_NOTIFICATIONS_PHASE5.md](ERP_NOTIFICATIONS_PHASE5.md) pour DELETE notifications, category, préférences mentions/invitations, mentionIds, invitations et leurs règles d’accès.
