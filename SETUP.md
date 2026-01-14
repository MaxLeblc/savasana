# 🚀 Quick Start Guide - Savasana Yoga Project

## Prerequisites Installed

- ✅ Node.js v16.20.2 (for Angular 14)
- ✅ OpenJDK 17 (for Spring Boot)
- ✅ Maven 3.9.12
- ✅ Docker + Docker Compose

## 📝 Starting the Project

### 1. Environment Variables

Copy the example environment file:
```bash
cp .env.example .env
```

Edit `.env` if needed to customize database and JWT settings.

### 2. Start MySQL Database

```bash
docker compose up -d
```

Verify MySQL is running:
```bash
docker compose ps
```

### 3. Start Backend (Spring Boot)

```bash
./start-back.sh
```

Backend will be accessible at: `http://localhost:8080`

### 4. Start Frontend (Angular)

```bash
./start-front.sh
```

Frontend will be accessible at: `http://localhost:4200`

> **Note**: The proxy automatically redirects `/api/*` calls to the backend on port 8080.

## ⚠️ Troubleshooting: Ports Already in Use

If you get a "Port already in use" error:

```bash
# Check what's using a port
lsof -i :8080    # Backend
lsof -i :4200    # Frontend
lsof -i :3306    # MySQL

# Stop services
pkill -f "spring-boot:run"  # Backend
pkill -f "ng serve"         # Frontend
docker compose down         # MySQL

# Or kill a specific process
kill $(lsof -t -i :8080)
```

## 🛠️ Useful Commands

### Docker MySQL

```bash
# Start
docker compose up -d

# Stop
docker compose down

# View logs
docker compose logs -f mysql

# Connect to MySQL
docker compose exec mysql mysql -u root -p\${MYSQL_ROOT_PASSWORD} \${MYSQL_DATABASE}

# Complete reset (deletes data)
docker compose down -v
docker compose up -d
```

### Backend

```bash
cd back
mvn clean install        # Compile
mvn test                 # Tests
mvn spring-boot:run      # Run
```

### Frontend

```bash
cd front
npm test                 # Jest tests without coverage
npm test -- --coverage   # Jest tests with coverage
```

## 🧪 Tests

### Unit Tests (Jest)

```bash
./run-jest.sh
```

✅ Generates coverage report in `front/coverage/jest/lcov-report/index.html`

### E2E Tests (Cypress)

**Automatic mode (recommended)**
```bash
./start-back.sh      # Terminal 1
./run-cypress.sh     # Terminal 2
```

The script automatically starts the frontend with instrumentation, runs tests, and generates coverage.

**Interactive mode (browser)**
```bash
./start-back.sh      # Terminal 1

# Terminal 2 - Frontend with instrumentation
cd front
export PATH="\$HOME/.config/nvm/versions/node/v16.20.2/bin:\$PATH"
npm run start:e2e

# Terminal 3 - Cypress interactive
cd front
export PATH="\$HOME/.config/nvm/versions/node/v16.20.2/bin:\$PATH"
npm run cypress:open

# After tests - View coverage summary
cd front && npx nyc report --reporter=text-summary
```

⚠️ **Important**: For Cypress coverage, the frontend must be started with `start:e2e` (not `start`).

### 📊 View Coverage Reports

```bash
./view-coverage.sh
```

Opens coverage reports in browser:
- **Jest**: `front/coverage/jest/lcov-report/index.html`
- **Cypress**: `front/coverage/lcov-report/index.html`

## 🔧 Configuration

**Database** (configured in `.env`)
- Host: localhost:3306
- Database: yoga
- User: root
- Password: root

**Default User**
- Email: yoga@studio.com
- Password: test!1234

## 🐛 Troubleshooting

**Docker not working**
```bash
groups                          # Must contain "docker"
sudo systemctl restart docker   # Restart service
```

**Port already in use**
```bash
lsof -i :8080                # See which process uses the port
kill $(lsof -t -i :8080)     # Kill it
```

**MySQL connection issues**
```bash
# Check MySQL logs
docker compose logs mysql

# Reset database
docker compose down -v
docker compose up -d
```
