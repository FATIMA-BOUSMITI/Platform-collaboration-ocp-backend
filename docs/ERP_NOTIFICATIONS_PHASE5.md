# Phase 5 — Notifications ERP complètes

Branche : feature/erp-collaboration-modules dans les deux dépôts. Travail précédent sauvegardé avant extension : backend bd16db8, frontend b2fffbc. Commit dédié demandé : feat: integrate real-time ERP notification module.

## Sources et décision présentées avant implémentation

| Repository | Licence | Fonction et maintenabilité examinées | Compatibilité / décision |
|---|---|---|---|
| [Sonner](https://github.com/emilkowalski/sonner) | MIT | Toasts React, historique de commits/releases et sources TypeScript disponibles | Déjà intégré, version verrouillée 2.0.8 ; conservé pour les alertes éphémères |
| [React-Toastify](https://github.com/fkhadra/react-toastify) | MIT | Toasts React et addon notification center ; historique de releases disponible | Compatible côté React mais recouvre Sonner ; pas de seconde bibliothèque de toasts ajoutée |
| [Novu](https://github.com/novuhq/novu) | MIT pour les composants communautaires ; licence commerciale pour enterprise | Inbox et infrastructure de notifications multi-canaux | Déploiement et stockage supplémentaires ; non importé dans les microservices Spring existants |
| [Spring Framework](https://github.com/spring-projects/spring-framework) / [documentation STOMP](https://docs.spring.io/spring-framework/reference/web/websocket/stomp.html) | Apache-2.0 | Infrastructure WebSocket/STOMP native de la stack déjà présente | Réutilisée avec STOMP.js ; pas de notification-service supplémentaire |

Le choix de maintenabilité est de conserver une seule implémentation persistante dans communication-service et les composants du design ERP. Les dépôts externes ne sont pas présentés comme fournissant la gestion des droits ou la persistance PostgreSQL métier : ces responsabilités restent dans le backend ERP. Les liens ont été consultés ; aucun code applicatif complet ni authentification externe n'est importé.

## Fonctionnalités ajoutées

- Cloche de navbar : compteur réel, aperçu des six notifications récentes, catégories, dates, chargement, état vide, lecture à l'ouverture d'une ressource, tout lire, suppression et lien vers le fil complet. Fermeture au clic extérieur, Escape et navigation ; ouverture accessible au clavier.
- Centre : historique PostgreSQL paginé 40, filtre non lu et catégories Toutes/Messages/Tâches/Mentions/Invitations/Échéances, lu/non lu, tout lire, suppression, préférences et liens contrôlés. Les alertes de sécurité admin existantes restent affichées dans une section distincte.
- Suppression logique deleted_at : exclusion des listes/compteurs et maintien de external_id unique. Rejouer le même événement ne recrée pas la notification supprimée. Version optimiste pour empêcher une lecture concurrente de restaurer une ligne supprimée.
- Préférences personnelles : messages, tâches, échéances, mentions, invitations, actives par défaut ; la préférence spécifique mention/invitation prime sur la catégorie d'origine. Les préférences gouvernent la création des nouvelles notifications, pas l'accès à une invitation métier en attente.
- Mentions : sélection explicite d'au plus 20 utilisateurs depuis le compositeur de message ou commentaire. Les identifiants sont envoyés dans mentionIds et les libellés @nom sont inscrits dans le texte conservé. Aucun parsing arbitraire d'un email ou envoi à un destinataire hors droits. Conversation : membres uniquement. Commentaire : responsable, personne affectée ou invitation acceptée au projet. L'auteur est exclu. Une mention remplace la notification générique pour ce destinataire afin d'éviter le doublon.
- Invitations projets : création par responsable autorisé, destinataire validé dans l'annuaire existant, notification durable, liste personnelle, acceptation/refus et annulation d'une invitation en attente par son responsable. Une invitation en attente ne donne pas accès au projet ; après acceptation, lecture du projet et de ses tâches et commentaires autorisés. Création/édition de tâches réservées aux responsables ; changements de statut aux responsables et à la personne affectée. Les invitations gardent leur historique métier. Aucun nouveau rôle global n'est créé.
- Les collaborateurs ayant accepté une invitation rejoignent les destinataires des événements de tâche et commentaires du projet. Les affectations initiales/modifiées, changements de statut et échéances restent reliés à l'outbox existante. Les rappels visent la personne affectée. Les messages privés et de groupe produisent des notifications après persistance.

## Contrats ajoutés ou enrichis

Toutes les routes publiques ci-dessous sont authentifiées, via la gateway. L'identifiant propriétaire des notifications est tiré du JWT, jamais d'un paramètre utilisateur.

| Méthode | Route | Contrat |
|---|---|---|
| GET | /api/notifications?unread=false&page=0&category=ALL | 40 éléments visibles maximum ; catégories ALL,MESSAGES,TASKS,MENTIONS,PROJECTS,DEADLINES |
| GET | /api/notifications/unread-count | count des lignes personnelles non lues et non supprimées |
| PATCH | /api/notifications/{id}/read | objet {read:true} ou {read:false} |
| PATCH | /api/notifications/read-all | lecture globale des notifications personnelles visibles |
| DELETE | /api/notifications/{id} | 204 ; propriétaire uniquement ; 404 pour un UUID appartenant à autrui ou déjà supprimé |
| GET, PUT | /api/notifications/preferences | messages,tasks,deadlines,mentions,invitations ; userId du corps est ignoré/remplacé |
| POST | /api/conversations/{id}/messages | senderId,content,mentionIds facultatif ; membres mentionnés validés côté serveur |
| POST | /api/tasks/{id}/comments | authorId,content,mentionIds facultatif ; droits de chaque destinataire vérifiés |
| GET | /api/tasks/{id}/mention-candidates | UUID de collaborateurs mentionnables, après contrôle de lecture de la tâche |
| POST, GET | /api/projects/{id}/invitations | création {userId} / historique ; responsable autorisé uniquement |
| DELETE | /api/projects/{projectId}/invitations/{id} | annulation d'une invitation encore en attente |
| GET | /api/project-invitations | invitations PENDING de l'utilisateur connecté |
| PATCH | /api/project-invitations/{id} | {accept:true} ou {accept:false} ; destinataire uniquement |

Nouveaux types de notifications : MENTION,PROJECT_INVITATION. Les liens vers une invitation pointent sur /notifications#invitation-UUID, où ses actions sont disponibles même si les notifications d'invitation sont désactivées. Après acceptation, lien vers /projects?project=UUID. Les autres liens existants vers tâche/conversation sont conservés.

Le WebSocket reste /ws, avec CONNECT JWT et uniquement /user/queue/messages et /user/queue/notifications. Le signal notification-state resynchronise les compteurs et listes après lecture ou suppression, y compris depuis un autre onglet. Les événements tâches/invitations sont persistés dans l'outbox de leur transaction ; retry et déduplication assurent leur conservation quand le destinataire est déconnecté. Les fichiers ne sont pas transportés par WebSocket.

## Migrations fournies, non exécutées

Après les scripts 01 puis 02 déjà documentés, appliquer sur les bases/schémas respectifs :

- communication-service/src/main/resources/db/migration/V20261020_03__notification_controls.sql : deleted_at/version, préférences mentions/invitations et index des notifications visibles.
- project-management-service/src/main/resources/db/migration/V20261020_03__project_invitations.sql : table/index des invitations et élargissement non destructif des commentaires en text. La clé étrangère d'invitation est supprimée en cascade seulement si le projet est explicitement supprimé ultérieurement.

Soit six scripts au total pour l'intégration complète 01/02/03 dans chaque service. Aucun script SQL n'a été exécuté et aucun moteur de migration n'a été installé automatiquement. Contrôler le schéma réel et la sauvegarde avant application. Configuration supplémentaire : user.service.url=http://localhost:8082 côté projets ; collaboration.projects-http-uri=http://localhost:8083 dans la gateway pour /api/project-invitations. Configurer un volume de fichiers persistant comme décrit dans le rapport général.

## Vérification et QA reportée

Seules la cohérence des sources et les compilations npm run build et Maven -Dmaven.test.skip=true compile sont utilisées. Aucun test ni appel API de validation n'est exécuté. Les migrations, requêtes JPQL, échanges WebSocket, accès croisés et rendu dans un navigateur n'ont donc pas été validés en exécution.

À vérifier lors de la future QA : isolation liste/count/read/delete/preferences pour deux utilisateurs, conflits lecture/suppression, filtrage et pagination, doublons après replay d'événement y compris après suppression, notification hors ligne puis reconnexion, préférences activées/désactivées, mentions hors membres refusées, une seule notification pour une mention, changements d'affectation/statut, invitations créer/accepter/refuser/annuler et accès avant/après acceptation, URLs directes et ressources supprimées, expiration/révocation/reconnexion, tous les rôles, clavier/Escape/clic extérieur et petits écrans. La déduplication des événements n'est pas une clé d'idempotence globale des commandes métier HTTP ; répéter manuellement une création de message reste une nouvelle commande.

Limites conservées : broker simple en mémoire (une instance), pas de pagination de l'historique messages préexistant, stockage de fichiers à configurer et absence d'analyse antivirus. La compilation ne prouve pas le fonctionnement réel de ces échanges. Aucun push ni fusion n'est effectué sans accord.
