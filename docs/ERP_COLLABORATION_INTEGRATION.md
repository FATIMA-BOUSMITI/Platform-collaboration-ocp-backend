# Intégration des modules de collaboration ERP

Date : 20 octobre 2026. Branche locale dans les deux dépôts : feature/erp-collaboration-modules.

## Périmètre et sauvegarde

Le travail existant a été sauvegardé avant l'intégration : backend e7254d2, frontend 512d00e. Les configurations locales contenant potentiellement des identifiants, .env, IDE et fichiers générés sont exclus des nouveaux commits. Les tests existants sont conservés. Aucun test unitaire, API, intégration, performance ou E2E n'a été créé ou exécuté pendant cette intégration. Les builds de compilation seuls sont autorisés.

Les cibles de PR restent backend master2 et frontend main. Le backend master utilise une structure utilisateurs différente : cette intégration conserve UserProfile, Departement et Team de master2. Aucun push, aucune migration exécutée et aucune fusion n'ont été réalisés pour cette étape.

Historique pertinent examiné dans les dépôts locaux : backend PR #12 (project-management-service), PR #11/#10 (auth), frontend PR #11/#10 (role-based-access). Il s'agit des PR fusionnées présentes dans l'historique, pas d'une affirmation sur la liste actuelle des PR ouvertes sur GitHub.

## Choix open source

| Usage | Source | Licence | Choix d'intégration |
|---|---|---|---|
| Kanban | https://github.com/clauderic/dnd-kit | MIT | @dnd-kit/core 6.3.1, sortable 10.0.0, utilities 3.2.2 : capteurs pointeur/clavier et cartes triables ; domaine projets conservé côté Spring |
| Temps réel | https://github.com/stomp-js/stompjs | Apache-2.0 | @stomp/stompjs 7.5.0 ; protocole STOMP avec Spring WebSocket, connexion JWT existante |
| Notifications visuelles | https://github.com/emilkowalski/sonner | MIT | sonner 2.0.8 ; toasts uniquement, persistance et droits gérés par le backend ERP |
| Alternative Kanban | https://github.com/hello-pangea/dnd | Apache-2.0 | Modèle de listes utile mais dnd-kit retenu pour colonnes et capteurs personnalisés |
| Alternative chat | https://github.com/chatscope/chat-ui-kit-react | MIT | UI complète imposant davantage de styles et dépendances ; composants ERP conservés |
| Alternative notifications | https://github.com/novuhq/novu | Licences à examiner selon les composants | Infrastructure plus large que le besoin ; pas de second stockage/authentification importé |

La comparaison et le plan ont été présentés avant les modifications fonctionnelles. Les bibliothèques sont réutilisées comme dépendances npm, sans copier des applications complètes. Les versions effectives sont verrouillées dans package-lock.json. Les textes de licence des dépendances retenues sont archivés dans docs/licenses côté frontend. La compatibilité technique a été contrôlée par installation et compilation React 19/TypeScript ; cela ne remplace pas la QA ni une revue de maintenance future.

## Architecture conservée

- auth-service (8080) : identité, JWT, renouvellement, session persistée, rôle et permissions.
- User-service (8082) : profils, départements, équipes ; nouvel annuaire authentifié limité à id/prénom/nom/email et id/nom de département.
- project-management-service (8083) : projets, tâches, commentaires, sous-tâches et calendrier existants ; ajout des colonnes Kanban, ordre/version et outbox durable.
- communication-service (8089) : conversations et messages existants ; ajout WebSocket, lectures, fichiers, notifications persistées et préférences.
- api-gatway (9000) : nouvelles routes Java /ws et /api/notifications ; routes REST existantes conservées.
- Frontend : une connexion temps réel globale par utilisateur, nouveaux écrans chargés à la demande, shell partagé pour les espaces direction/manager/employé. Le menu admin demandé est conservé et réutilisé dans ses modules collaboratifs.

Les identifiants UUID des profils existants restent la référence commune ; aucune seconde base d'utilisateurs n'est introduite. Les relations locales PostgreSQL sont conservées. Les références entre microservices restent des UUID, sans clés étrangères entre bases.

## Comportements implémentés

Kanban : projets accessibles selon le rôle, colonnes personnalisées liées à un statut métier, création/édition de tâche, priorité, affectation, date, estimation, filtres, commentaires, checklist, archivage, déplacement clavier/pointeur. Le déplacement transmet expectedVersion ; le serveur refuse les conflits et le frontend recharge après échec. Les tâches historiques sans columnId sont affichées dans la première colonne de leur statut. Une colonne occupée ne peut pas être supprimée.

Messagerie : conversations privées à deux membres, groupes et discussions de projet, annuaire réel, historique et recherche dans les messages chargés, participants, envoi REST persistant puis signal WebSocket après commit. Le créateur d'une conversation projet doit pouvoir lire ce projet. Le groupe possède ensuite sa propre liste de membres : aucun partage automatique de documents ou des droits du projet. Ajout de participants réservé aux administrateurs de groupe. Téléchargement via requête authentifiée. Taille 10 Mo/fichier et cinq fichiers/message côté serveur ; sélection d'un fichier par envoi dans l'interface. Le retry d'un fichier n'envoie pas à nouveau le texte déjà enregistré.

Notifications : nouveaux messages, création/affectation, mise à jour, changement de statut et commentaire de tâche, échéance. Destinataires tâches : responsable du projet et personne affectée, hors auteur ; échéance : personne affectée. Fil d'historique paginé 40, lu/non lu, tout lire, compteur et préférences messages/tâches/échéances/mentions/invitations. Les événements tâches sont persistés avec la transaction métier puis livrés avec retry exponentiel. Le service de réception déduplique l'identifiant d'événement. Les échéances arrivent par ordonnanceur toutes les cinq minutes, jusqu'au lendemain et pour les tâches non terminées/non archivées.

## Authentification et temps réel

Endpoint navigateur : ws://localhost:9000/ws en développement, wss en HTTPS. VITE_WS_URL peut remplacer l'URL déduite de VITE_API_URL. Le handshake HTTP autorise l'upgrade, puis STOMP CONNECT exige Authorization: Bearer <access-token>. La signature, le type ACCESS et la session via /api/auth/access-context sont vérifiés. Les abonnements autorisés sont exclusivement /user/queue/messages et /user/queue/notifications. STOMP SEND est refusé : toutes les mutations passent par REST.

Les sessions WebSocket sont revérifiées toutes les 30 secondes ; expiration ou révocation ferme la connexion. Une indisponibilité transitoire du service auth ne déconnecte pas une session JWT encore valide. Le client renouvelle/reconnecte avant expiration, sans transformer une panne WebSocket en logout.

L'outbox contacte directement le service communication via /api/internal/collaboration/events. Ce point d'entrée vérifie un JWT de service de 30 secondes (type COLLABORATION_SERVICE, issuer project-management-service, serviceAudience communication-service), distinct des JWT utilisateur. Il n'a pas de nouvelle route publique gateway. La clé JWT existante reste en configuration locale et n'est pas documentée ici.

## Configuration ajoutée

| Propriété | Valeur de développement par défaut | Usage |
|---|---|---|
| auth.service.url | http://localhost:8080 | introspection de session communication |
| project.service.url | http://localhost:8083 | autorisation du projet lié à une conversation |
| collaboration.service.url | http://localhost:8089 | destination de l'outbox projets |
| collaboration.communication-http-uri | http://localhost:8089 | route notifications gateway |
| collaboration.communication-ws-uri | ws://localhost:8089 | route WebSocket gateway |
| collaboration.allowed-origins | localhost et 127.0.0.1 ports 5173/5174 | origines WebSocket autorisées |
| collaboration.storage.path | java.io.tmpdir/ocp-collaboration | stockage des fichiers : configurer un volume persistant avant déploiement |
| collaboration.outbox.delay | 5000 ms | intervalle de livraison |
| collaboration.deadlines.delay | 300000 ms | intervalle de rappel |

Ne pas activer le profil local permissif pour une validation des permissions ou un déploiement. Définir les origines et adresses correspondant à l'environnement réel ; aucune origine wildcard n'est ajoutée.

## Migrations non exécutées

Les scripts sont versionnés mais aucun moteur Flyway n'est ajouté automatiquement à cette architecture existante. Ils doivent être appliqués par le processus de migration retenu, sur chaque base/schéma du service correspondant, après sauvegarde et inspection de son schéma réel. Ne pas laisser ddl-auto=update remplacer une procédure de migration contrôlée en production.

| Service | Script relatif à src/main/resources/db/migration | Effet |
|---|---|---|
| projets | V20261020_01__collaboration_kanban.sql | board_columns, task.column_id/sort_index/version, affectation et échéance facultatives, index |
| projets | V20261020_02__collaboration_outbox.sql | table et index outbox |
| communication | V20261020_01__messaging_realtime.sql | project_id, last_read_at, métadonnées pièces jointes et index |
| communication | V20261020_02__collaboration_notifications.sql | notifications, préférences et index |

Ordre : scripts 01 puis 02 puis 03 dans chacun des deux services, puis démarrer les services compilés et le frontend. Ils n'effacent aucune table ni ligne métier. La suppression de NOT NULL relaxe les contraintes de tâche pour autoriser un brouillon non affecté/sans date. Vérifier les noms de tables historiques project/task/conversations/messages avant application ; les scripts ciblent les mappings de master2. Le retour applicatif doit conserver les nouvelles colonnes/tables plutôt que supprimer les données de collaboration.

## Vérification effectuée et limites

Compilations uniquement : npm run build ; mvn -q -Dmaven.test.skip=true compile pour User-service, project-management-service, communication-service et api-gatway. Le skip exclut aussi la compilation des tests existants, qui pourront nécessiter une adaptation de leurs fixtures lors de la phase QA. Aucun démarrage de service ni requête métier de validation n'a été effectué pour cette intégration. Les succès de compilation ne prouvent pas l'exécution des endpoints, des migrations ou des WebSockets.

QA différée : connexion/refresh/révocation dans les quatre rôles, droits d'accès croisés et multi-utilisateurs, conflits de drag-and-drop, colonnes et archivage historiques, commentaires/checklists, échanges et lectures sur deux sessions, fichiers autorisés/refusés/retry, notifications et préférences, reprise de l'outbox après panne, déduplication et fuseaux horaires, navigation des deep links, responsive et accessibilité clavier/mobile. Vérifier également les anciens tableaux de bord et le module documents conservés : leur disponibilité dépend des services préexistants.

Limites de déploiement : broker WebSocket simple en mémoire adapté à une instance ; plusieurs instances nécessitent un broker partagé et un stockage de fichiers commun. L'historique messages utilise l'API existante non paginée et doit être borné/paginé avant de très gros volumes. La liste MIME des fichiers ne constitue pas une analyse antivirus. Aucune preuve de bon fonctionnement en environnement réel n'est revendiquée à cette étape.

## Extension Phase 5

Les contrôles de suppression, catégories, mentions et invitations sont décrits dans [ERP_NOTIFICATIONS_PHASE5.md](ERP_NOTIFICATIONS_PHASE5.md), qui complète ce rapport. Six scripts 01/02/03 sont fournis au total. Ils restent non exécutés.
