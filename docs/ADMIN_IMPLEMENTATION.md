# Espace administrateur

Le frontend utilise exclusivement les API pour les utilisateurs, rôles, permissions, départements, équipes, sessions, préférences et notifications. Le style suit les captures fournies : navigation horizontale, menus déroulants, cartes blanches, bordures vert clair, fond gris clair et disposition mobile.

## Démarrage

1. Démarrer auth-service sur 8080, User-service sur 8082 et api-gatway sur 9000 avec les configurations existantes.
2. Dans le frontend, lancer npm run dev. VITE_API_URL peut remplacer http://localhost:9000/api.
3. Les schémas sont actuellement gérés avec spring.jpa.hibernate.ddl-auto=update. Si ce mécanisme est désactivé, exécuter admin-auth-migration.sql dans auth_db et admin-organization-migration.sql dans organization_db avant démarrage.
4. Le catalogue des permissions métier est complété au démarrage sans modifier les affectations existantes.

## Comportement

- Une session correspond au refresh token persisté, identifié par un UUID. Les tokens bruts ne sont jamais renvoyés par les API admin.
- Les JWT d’accès sont liés à une session. La révocation et les changements d’état/rôle sont vérifiés en base par auth-service. User-service valide chaque session auprès d’auth-service et refuse l’accès si cette validation échoue.
- La politique existante d’une session renouvelable par compte reste conservée : une nouvelle connexion révoque les anciens refresh tokens.
- Le frontend partage un seul renouvellement entre les requêtes concurrentes, ne renouvelle pas un 403 et ne réessaie un 401 qu’une fois.
- Les administrateurs gardent leurs droits intrinsèques. Pour les autres rôles, les permissions appliquées aux API admin utilisent RESOURCE_ACTION : USER, DEPARTMENT, TEAM, ROLE, PERMISSION et READ, CREATE, UPDATE, DELETE. Les anciens noms non conformes ne sont pas supprimés mais ne constituent pas des droits implicites.
- Le dernier administrateur actif est protégé contre désactivation, verrouillage et retrait de rôle, avec verrouillage des lignes concernées. Les comptes administrateur restent protégés contre la suppression, conformément à la règle existante.
- Les rôles système ne peuvent être renommés/supprimés ni perdre leurs permissions existantes. La sauvegarde groupée des permissions est transactionnelle et détecte les modifications concurrentes.
- Un compte créé est désactivé/verrouillé et reçoit un mot de passe aléatoire inconnu. L’administrateur doit l’activer, le déverrouiller et envoyer un lien de définition de mot de passe. L’envoi nécessite la configuration SMTP existante.
- Les notifications correspondent aux tentatives de connexion échouées du compte connecté ; elles sont persistées et peuvent être marquées comme lues.
- Le dashboard affiche des métriques réelles et les 50 dernières connexions connues ; il ne présente aucun pourcentage de tendance inventé.
- Les préférences de langue servent au format des dates ; les libellés de l’espace admin restent en français. Le fuseau horaire est appliqué aux dates.

## Périmètre restant dépendant de l’infrastructure

Aucun service de sauvegarde/restauration vérifiable n’est configuré dans cette implémentation. Les actions de sauvegarde trompeuses ont été retirées. Une telle fonction nécessite un stockage cible, une politique de rétention et une procédure de restauration testée.

Le 2FA n’est pas implémenté par le service d’authentification existant : l’interface n’affiche plus d’état 2FA fictif. Les validations de révocation immédiate ajoutées concernent auth-service et User-service ; les autres microservices gardent leurs filtres existants.

Les services de profil et d’identité utilisent deux bases distinctes : une panne au moment exact d’un commit interservices reste un cas de réconciliation distribuée, sans transaction XA ni outbox introduite ici.

## Vérifications

- Frontend : npm run build, npm run lint et npm run test:session.
- auth-service : mvn -Dtest=SecurityAuthorizationTest,AccessContextServiceTest,LastAdministratorTest,UserServiceTest,PasswordResetServiceTest,AdminPersistenceTest test.
- User-service : mvn -Dtest=SecurityAuthorizationTest,UserProfileServiceImplTest,TeamPersistenceTest test.
- Les tests de persistance utilisent H2 en mémoire, des transactions annulées et un rechargement du contexte de persistance. Ils ne modifient pas les bases PostgreSQL configurées.

Les tests UI avec réponses API contrôlées doivent être distingués d’un parcours complet sur les services réellement déployés. Les mises à jour de schéma ne sont pas appliquées manuellement aux bases distantes par ces tests.
