# Architecture backend

Le frontend [Platform-collaboration-ocp-frontend](https://github.com/FATIMA-BOUSMITI/Platform-collaboration-ocp-frontend) utilise la Gateway sur le port 9000. Le routage est défini dans `api-gatway/src/main/resources/application.yml` et les adresses des services sont externalisées.

## Routage principal

| Préfixe API | Service |
| --- | --- |
| `/api/auth/**`, `/api/roles/**`, `/api/permissions/**` | Authentification |
| `/api/users/**` | Organisation |
| `/api/projects/**`, `/api/tasks/**`, `/api/subtasks/**`, `/api/calendar/**` | Projets |
| `/api/conversations/**` | Communication |
| `/api/documents/**` | Documents |

Les modules temps réel et notifications ont aussi des configurations dédiées : voir [les contrats collaboratifs](../COLLABORATION_API.md) et [la documentation de notifications](../ERP_NOTIFICATIONS_PHASE5.md). Vérifier les routes du code exécuté : une déclaration de contrat ne constitue pas une preuve de disponibilité d'un endpoint.

## Authentification

Le service d'authentification expose les opérations de connexion, déconnexion, renouvellement du token, récupération et réinitialisation du mot de passe. Les clients doivent utiliser de vrais tokens émis par l'environnement cible. Le frontend adapte sa navigation au rôle, mais la vérification d'autorisation reste une responsabilité serveur.

## Persistance et intégrations

Les cinq services métier ont chacun une cible PostgreSQL configurable. Le Compose ne crée pas ces bases. Le service organisation et les modules collaboratifs utilisent aussi des appels interservices vers l'authentification et, selon leur fonction, vers les projets.

Les paramètres mail et stockage documentaire sont privés. Le build d'une image ne prouve ni l'accessibilité d'une base ni le fonctionnement d'un fournisseur externe.

## Versions et modules

Les modules métier utilisent Spring Boot 3.5.3 et Java 17 ; la Gateway déclare Spring Boot 3.5.4. Consulter chaque `pom.xml` avant de généraliser une version à tout le backend.

## Validation

Le [projet QA](https://github.com/FATIMA-BOUSMITI/Collaborative-Platform-QA) contient les scénarios transversaux et leur traçabilité. Les captures et statuts de la [démonstration](DEMONSTRATION.md) restent distincts des tests Java et des contrôles CI.
