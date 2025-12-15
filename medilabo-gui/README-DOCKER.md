# Déploiement Docker (stack HTTPS direct)

Ce dossier contient un `docker-compose.yml` qui orchestre l'ensemble des services MediLabo. La GUI expose directement HTTPS (sans Nginx) et les services internes communiquent en HTTPS.

## Prérequis
- Docker et Docker Compose
- Une base MongoDB Atlas (conservée) et son URI
- Un certificat TLS (clé privée + fullchain) pour le reverse proxy

## Arborescence
- `Dockerfile` : image de l'application GUI (HTTP interne)
- `docker-compose.yml` : orchestre GUI, gateway, back, back-mongo, back-risk (sans reverse proxy)
- `.env.example` : variables d'environnement (copier en `.env`)

## Variables d'environnement
Copiez `.env.example` en `.env` et complétez :

- `MONGODB_URI` : URI MongoDB Atlas de votre base
- `SERVER_NAME` : nom de domaine pour Nginx (ex: `example.com` ou `localhost`)
- `SPRING_PROFILES_ACTIVE` : profile Spring optionnel (par défaut `default`)

## Certificats TLS
Chaque service Spring Boot intègre un `keystore.p12` (classpath) utilisé pour activer HTTPS directement.

## Lancer la stack
Depuis ce dossier (`medilabo-gui`) :

```powershell
# 1) Copier le .env
Copy-Item .env.example .env -Force
# 2) Éditer .env et renseigner MONGODB_URI
# 3) Démarrer en arrière-plan
docker compose up -d --build
```

- L'extérieur accède à la GUI via HTTPS (port 443 mappé sur la GUI)
- Le trafic interne entre services est en HTTPS

## Arrêt et nettoyage
```powershell
docker compose down
```

## Notes
- Les URLs inter-services sont injectées via variables d'environnement et pointent sur les hostnames Docker:
  - `BACK_URL=https://medilabo-back:8082`
  - `MONGO_URL=https://medilabo-back-mongo:8083`
  - `RISK_URL=https://medilabo-back-risk:8084`
  - `GATEWAY_URL=https://medilabo-gateway:8090`
- L'URI MongoDB Atlas n'est pas fournie par défaut; renseignez `MONGODB_URI` dans `.env`.
- Des healthchecks HTTP peuvent être ajoutés ultérieurement si on embarque `curl`/`wget` dans les images, mais ils ne sont pas requis pour un démarrage correct.

## Sécurité: rotation des identifiants MongoDB Atlas
Si un identifiant/mot de passe Atlas a été exposé (même brièvement), il est recommandé de le ROTER.

Étapes (UI MongoDB Atlas):
1. Ouvrez votre projet Atlas → Database Access.
2. Créez un nouvel utilisateur (SCRAM) avec un mot de passe fort.
  - Rôles recommandés: `readWrite` sur la base `medilabo` (ou équivalent minimal nécessaire).
3. Copiez l'URI mise à jour (utilisateur/mot de passe neufs) dans `medilabo-gui/.env` (variable `MONGODB_URI`).
4. Redéployez la stack:
  - `docker compose up -d --build` depuis `medilabo-gui`.
5. Vérifiez le bon fonctionnement (GUI + opérations sur données).
6. Supprimez l'ancien utilisateur dans Atlas une fois la bascule validée.


## Logging (développement)

- Les applications écrivent des logs JSON dans `/var/log/medilabo/*.log` (console + fichiers tournants).
--
Bonnes pratiques:
- Ne commitez jamais le fichier `.env` ni les certificats TLS. Un `.gitignore` local est fourni.
- Utilisez `.env.example` (placeholders) pour documenter les variables requises.
- Si votre mot de passe contient des caractères spéciaux, encodez-le pour l'URI.

Encodage URL (optionnel, PowerShell Windows):
```powershell
Add-Type -AssemblyName System.Web
$plain = Read-Host -AsSecureString | ForEach-Object { [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($_)) }
[System.Web.HttpUtility]::UrlEncode($plain)
```
Collez la valeur encodée dans `MONGODB_URI` (à la place du mot de passe en clair.)
