# Configurer, construire et lancer le backend

## Prérequis

- JDK 17 et Maven pour l'exécution native.
- Docker Desktop/Engine et Compose pour les images.
- Bases PostgreSQL et comptes adaptés pour les services métier.
- Comptes applicatifs de démonstration déjà provisionnés si un parcours connecté est attendu.

## Configuration privée

```powershell
Copy-Item .env.example .env.local
```

Renseigner les variables du modèle avec des valeurs propres à l'environnement :

| Famille | Variables principales |
| --- | --- |
| Authentification | `JWT_SECRET`, `JWT_ACCESS_EXPIRATION`, `JWT_REFRESH_EXPIRATION` |
| Bases par service | `AUTH_DATASOURCE_URL`, `USER_DATASOURCE_URL`, `PROJECT_DATASOURCE_URL`, `COMMUNICATION_DATASOURCE_URL`, `DOCUMENT_DATASOURCE_URL` |
| Accès PostgreSQL | `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` |
| Schéma | `SPRING_JPA_HIBERNATE_DDL_AUTO` |
| Notifications mail | `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` |
| Stockage externe selon le module | `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` |

Une URL de base est une URL JDBC PostgreSQL ; une URL HTTPS de frontend ou d'API ne la remplace pas. Les services exécutés dans Docker doivent pouvoir joindre l'hôte PostgreSQL configuré.

## Gestion du schéma

Le modèle fourni utilise `update`. Cette option peut modifier le schéma au démarrage. Pour une base déjà préparée dont on veut uniquement vérifier la compatibilité, configurer `validate` ; un schéma manquant ou incompatible empêchera le démarrage. Cela ne transforme pas l'application en accès métier en lecture seule.

Les migrations et le provisionnement initial doivent être adaptés à l'environnement. Ne pas appliquer automatiquement les scripts d'administration sur une base existante pour faire réussir une démonstration.

## Construire les six images

```powershell
docker compose --env-file .env.local build
```

L'option `--env-file` fournit les variables nécessaires à l'interpolation du Compose. Le `env_file` déclaré dans chaque service injecte les variables à l'exécution : ces deux usages sont complémentaires.

## Démarrer et diagnostiquer

```powershell
docker compose --env-file .env.local up -d
docker compose --env-file .env.local ps
docker compose --env-file .env.local logs --tail=100 gateway-service
docker compose --env-file .env.local logs --tail=100 user-service
```

Le Compose publie les ports 9000, 8080, 8082, 8083, 8089 et 8079. Il ne contient ni serveur PostgreSQL ni frontend.

`depends_on` ordonne le lancement ; il n'atteste pas que la base et les API sont prêtes. Vérifier les réponses réelles des routes et les logs privés avant d'annoncer un démarrage fonctionnel.

Le service utilisateur active actuellement le profil **`local`**, décrit dans le Compose comme un contournement local d'un rejet de la chaîne de sécurité. Cette configuration n'est pas une validation des autorisations de production et doit être revue avant un déploiement partagé.

Arrêt :

```powershell
docker compose --env-file .env.local stop
```

## Exécution native

Chaque module possède son propre `pom.xml`. Depuis le module concerné :

```powershell
mvn test
mvn -DskipTests package
mvn spring-boot:run
```

Configurer ses variables Spring, sa base et les URLs interservices avant le démarrage. Les noms `auth-service`, `user-service`, etc. sont des noms DNS Docker : en natif, utiliser les adresses locales adaptées à chaque service.

Commencer par l'authentification et les services requis, puis la Gateway. Pour le frontend, suivre [son guide](https://github.com/FATIMA-BOUSMITI/Platform-collaboration-ocp-frontend/blob/main/docs/presentation/GETTING_STARTED.md).

## Périmètre de la documentation

Les commandes ci-dessus décrivent les fichiers du dépôt ; la mise à jour documentaire ne revendique pas un nouveau build ni une nouvelle suite Maven réussie. La démonstration capturée a été exécutée nativement, Docker étant indisponible, et ses limites sont consignées dans [le dossier de preuves](DEMONSTRATION.md).
