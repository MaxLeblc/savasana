# 🚀 Guide de démarrage - Projet Savasana Yoga

## ✅ Configuration terminée

Tous les outils ont été installés sur votre système CachyOS:

- ✅ Node.js v16 (pour Angular 14)
- ✅ Node.js v25.2.1 (système)
- ✅ npm v11.7.0
- ✅ OpenJDK 17 (pour Spring Boot)
- ✅ OpenJDK 25.0.1 (système)
- ✅ Maven 3.9.12
- ✅ Docker + Docker Compose

## 📝 Étapes pour démarrer le projet

### 1. **IMPORTANT** - Activer Docker

Vous devez vous **déconnecter et reconnecter** de votre session pour que Docker fonctionne sans sudo.

Après reconnexion, vérifiez avec:

```bash
docker ps
```

### 2. Démarrer la base de données MySQL

```bash
# À la racine du projet
docker compose up -d
```

Cela va:

- Télécharger l'image MySQL 8.0
- Créer la base de données `yoga`
- Exécuter automatiquement le script `ressources/sql/script.sql`
- Exposer MySQL sur le port 3306

Pour vérifier que MySQL est bien démarré:

```bash
docker compose ps
docker compose logs mysql
```

### 3. Démarrer le backend (Spring Boot)

**Option 1 : Avec le script**
```bash
./start-back.sh
```

**Option 2 : Manuellement**
```bash
cd back
mvn spring-boot:run
```

Le backend sera accessible sur: `http://localhost:8080`

### 4. Démarrer le frontend (Angular)

**⚠️ IMPORTANT : Le frontend doit être lancé avec Node 16 et le proxy activé !**

**Option 1 : Avec le script (recommandé)**
```bash
./start-front.sh
```

**Option 2 : Manuellement**
```bash
cd front
nvm use 16
npm run start -- --proxy-config src/proxy.config.json
```

Le frontend sera accessible sur: `http://localhost:4200`

> **Note** : Le proxy (`src/proxy.config.json`) redirige automatiquement les appels `/api/*` vers le backend sur le port 8080.

## ⚠️ Attention : Arrêter les services avant de relancer

Si vous obtenez une erreur "Adresse déjà utilisée" / "Port already in use" :

### Voir ce qui tourne sur les ports

```bash
# Voir tous les ports en écoute avec les processus
sudo ss -tulpn | grep -E ':(3306|8080|4200)'

# Voir ce qui utilise un port spécifique
lsof -i :8080    # Backend
lsof -i :4200    # Frontend
lsof -i :3306    # MySQL

# Voir tous les processus Java et Node
ps aux | grep -E "(java|node)" | grep -v grep
```

### Arrêter les services

```bash
# Arrêter le backend (port 8080)
pkill -f "spring-boot:run"

# Ou tuer un processus spécifique par PID
lsof -i :8080  # noter le PID dans la colonne 2
kill <PID>

# Arrêter le frontend (port 4200)
pkill -f "ng serve"

# Arrêter MySQL
docker compose down
```

### Exemple complet pour identifier et tuer

```bash
# 1. Identifier le processus sur le port 8080
lsof -i :8080
# Résultat : java  12345  max  ...

# 2. Tuer le processus
kill 12345

# Ou en une commande
kill $(lsof -t -i :8080)
```

## 🛠️ Commandes utiles

### Docker MySQL

```bash
# Démarrer
docker compose up -d

# Arrêter
docker compose down

# Voir les logs
docker compose logs -f mysql

# Se connecter à MySQL
docker compose exec mysql mysql -u root -proot yoga

# Réinitialiser complètement (supprime les données)
docker compose down -v
docker compose up -d
```

### Backend

```bash
cd back

# Compiler
mvn clean install

# Lancer les tests
mvn test

# Lancer l'application
mvn spring-boot:run
```

### Frontend

**⚠️ IMPORTANT : Utilisez Node 16 pour les commandes frontend**

```bash
cd front

# S'assurer d'utiliser Node 16
nvm use 16

# Installer les dépendances (déjà fait)
npm install

# Lancer en dev avec proxy
npm run start -- --proxy-config src/proxy.config.json

# Lancer les tests unitaires Jest
npm run test                    # Sans coverage
npm test -- --coverage          # Avec coverage

# Lancer les tests e2e avec Cypress (nécessite backend + frontend en cours d'exécution)
npm run cypress:run             # Mode headless
npm run cypress:open            # Mode interactif
npm run e2e:coverage            # Générer le rapport de coverage après les tests
```

**💡 Astuce** : Utilisez plutôt les scripts à la racine du projet (./start-back.sh, ./run-jest.sh, etc.) qui gèrent automatiquement les versions Java/Node !

## 🔧 Configuration

### Base de données

- **Host**: localhost
- **Port**: 3306
- **Database**: yoga
- **User**: root
- **Password**: root

Configuration dans: `back/src/main/resources/application.properties`

### Utilisateur par défaut

- **Email**: yoga@studio.com
- **Mot de passe**: test!1234

## 🧪 Tests

### Tests unitaires (Jest)

Les tests Jest fonctionnent avec **Node 16** et génèrent automatiquement un rapport de **coverage**.

**Option 1 : Avec le script (recommandé - génère le coverage)**
```bash
./run-jest.sh
```
✅ Génère le rapport de coverage dans `front/coverage/jest/lcov-report/index.html`

**Option 2 : Manuellement**
```bash
cd front
nvm use 16
npm run test              # Sans coverage
npm test -- --coverage    # Avec coverage
```

### Tests e2e (Cypress)

Les tests Cypress nécessitent que **le backend ET le frontend** soient en cours d'exécution.

**Étape 1 : Lancer les services**
```bash
# Terminal 1 : Backend
./start-back.sh

# Terminal 2 : Frontend  
./start-front.sh
```

**Étape 2 : Lancer les tests**

**Option 1 : Avec le script (recommandé - génère le coverage)**
```bash
./run-cypress.sh
```

Le script :
- ✅ Vérifie que les services tournent (ports 8080 et 4200)
- ✅ Lance les tests Cypress en mode headless
- ✅ Génère automatiquement le rapport de coverage
- ✅ S'arrête proprement à la fin

**Option 2 : Manuellement**
```bash
cd front
nvm use 16
npm run cypress:run       # Mode headless
# ou
npm run cypress:open      # Mode interactif (pas de coverage auto)
npm run e2e:coverage      # Générer le coverage après les tests
```

### 📊 Voir les rapports de coverage

```bash
./view-coverage.sh
```

Cela affichera les chemins vers :
- **Jest** : `front/coverage/jest/lcov-report/index.html`
- **Cypress** : `front/coverage/lcov-report/index.html`

Ouvrez ces fichiers dans votre navigateur pour voir les rapports détaillés de couverture de code.

## 📁 Structure du projet

```
savasana/
├── back/          # Backend Spring Boot
├── front/         # Frontend Angular
├── ressources/    # Scripts SQL et Postman
└── docker-compose.yml  # Configuration MySQL
```

## 🐛 Dépannage

### Docker ne fonctionne pas

1. Vérifiez que vous êtes bien déconnecté puis reconnecté
2. Vérifiez: `groups` (doit contenir "docker")
3. Si besoin, redémarrez le service: `sudo systemctl restart docker`

### MySQL ne démarre pas

```bash
# Voir les logs
docker compose logs mysql

# Réinitialiser
docker compose down -v
docker compose up -d
```

### Port déjà utilisé

```bash
# Vérifier les ports utilisés
sudo ss -tulpn | grep -E ':(3306|8080|4200)'

# Arrêter le service
docker compose down
```
