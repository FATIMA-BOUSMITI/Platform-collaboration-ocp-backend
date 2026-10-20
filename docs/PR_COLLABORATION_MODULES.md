# Intégrer Kanban, messagerie et notifications collaboratives

Branche source : feature/erp-collaboration-modules. Cible : master2. Dépôt : backend.

Les tâches utilisent désormais des colonnes persistées et un ordre modifiable, les conversations reçoivent leurs messages après commit via WebSocket, et les événements réels produisent des notifications persistées. L'authentification et les modèles locaux existants sont conservés. Les différents espaces partagent les éléments visuels et une navigation responsive ; le menu administrateur de référence est préservé.

Sources : dnd-kit (MIT), STOMP.js (Apache-2.0), Sonner (MIT). Les licences, la comparaison et l'architecture figurent dans [le rapport](ERP_COLLABORATION_INTEGRATION.md). Les payloads et routes sont documentés dans [les contrats API](COLLABORATION_API.md).

Déploiement coordonné avec l'autre dépôt : appliquer les six scripts SQL documentés sur les bases respectives avant démarrage, configurer les adresses des services, origines WebSocket et volume de fichiers persistant. Scripts fournis, non exécutés ; aucun effacement de données métier.

Validation limitée à la compilation TypeScript/Vite et Maven avec -Dmaven.test.skip=true. Aucun test automatisé ou API exécuté à la demande de l'utilisatrice ; tests existants préservés. QA différée explicitée dans le rapport, notamment rôles, migrations, WebSocket multi-utilisateurs, fichiers, reprise des événements et responsive. Aucune validation fonctionnelle en exécution n'est affirmée.

Publication en attente d'autorisation : ce document est le corps de PR préparé localement. Aucun push ni fusion effectués.

La phase 5 ajoute le menu de cloche, les catégories, la suppression logique, les mentions et le cycle complet d’invitations. Voir [le rapport notifications](ERP_NOTIFICATIONS_PHASE5.md). Commit dédié : feat: integrate real-time ERP notification module.
