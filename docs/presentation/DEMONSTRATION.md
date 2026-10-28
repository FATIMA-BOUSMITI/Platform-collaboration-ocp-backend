# Démonstration du backend et de son intégration

Ce dossier distingue une capture réelle de documentation Swagger, une capture documentaire de synthèse et un écran du frontend consommateur. Aucun de ces visuels n'est présenté comme un rapport de validation exhaustive.

## 1. Documentation Swagger du service d'authentification

![Documentation Swagger authentifiée](screenshots/swagger-auth-service.png)

Capture réelle de `http://127.0.0.1:8080/swagger-ui/index.html`, obtenue après une connexion réelle du compte QA manager. Le service a répondu **200** et ses opérations OpenAPI sont affichées. Aucun token n'est visible et aucun endpoint d'écriture n'a été exécuté avec « Try it out ».

L'accès non authentifié avait répondu 401. Le chargement authentifié de Swagger ne prouve pas que toutes les opérations documentées sont autorisées pour le compte utilisé ni qu'elles fonctionnent de bout en bout.

## 2. Observations HTTP pendant les parcours frontend

![Rapport documentaire des observations API](screenshots/api-observations.png)

Cette image est la capture d'un **rapport documentaire généré à partir du manifeste QA expurgé**. Ce n'est pas une interface native du backend, une capture de Postman ou un tableau de bord de production.

| Route | Statuts observés |
| --- | --- |
| `/api/auth/login` | 401, 200 |
| `/api/users/auth/:id` | 200, 504 |
| `/api/notifications/unread-count` | 404 |
| `/api/projects` | 500 |
| `/api/users/directory` | 200 |
| `/api/users/directory/departments` | 200 |

Deux comptes distincts ont été essayés : administrateur refusé et manager accepté. Le profil a répondu 200 lors d'une tentative et 504 lors d'une autre. Les identifiants de ressources sont remplacés par `:id` ; aucun corps de réponse, identifiant de compte ou token n'est publié.

- [Source JSON des observations](api-observations.json)
- [Rapport HTML de synthèse](api-observations.html) — télécharger ou ouvrir après clonage ; GitHub ne l'exécute pas comme une page Web.
- [Métadonnées de la capture Swagger](capture-metadata.json)

## 3. Frontend consommant l'API

![Tableau de bord manager après connexion](screenshots/frontend-manager.png)

Le tableau de bord a été atteint après une authentification réelle. Les données de compte sont masquées. Les [autres captures frontend](https://github.com/FATIMA-BOUSMITI/Platform-collaboration-ocp-frontend/blob/main/docs/presentation/INTERFACES.md) montrent également les formulaires et l'erreur réelle de chargement des projets.

## Environnement et limites

Les captures sont issues de la session locale du 28 octobre 2026. Le frontend de `Ger` contenait des modifications non commitées ; les services étaient des JAR déjà compilés. La correspondance exacte avec les commits publiés n'est pas établie.

Docker Desktop était indisponible : le backend a été lancé nativement en Java 17, avec Hibernate `validate`. Le service utilisateur utilisait le profil `local` existant ; cette démonstration ne valide pas la politique d'autorisation de production.

Les erreurs 500 sur les projets, 404 sur les notifications et le délai 504 sur le profil restent documentés. Aucune correction de code ni création de données métier n'a été effectuée pour embellir les captures. Les connexions ont pu créer des sessions et traces d'audit ; la session temporaire de capture Swagger a fait l'objet d'une demande de déconnexion.

## Liens vers les autres dépôts

- [Frontend : interfaces et configuration](https://github.com/FATIMA-BOUSMITI/Platform-collaboration-ocp-frontend)
- [QA : automatisation et résultats](https://github.com/FATIMA-BOUSMITI/Collaborative-Platform-QA)
- [QA : démonstration complète et galerie](https://github.com/FATIMA-BOUSMITI/Collaborative-Platform-QA/blob/main/docs/demo/README.md)
- [QA : synthèse des tests](https://github.com/FATIMA-BOUSMITI/Collaborative-Platform-QA/blob/main/docs/results/README.md)

Les tests QA historiques restent distincts des tests Java de ce backend et de ces captures de documentation.
