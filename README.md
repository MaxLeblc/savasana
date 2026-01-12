# 🚀 Getting Started Guide - Savasana Yoga Project

## ✅ Configuration Complete

All tools have been installed on your CachyOS system:

- ✅ Node.js v16 (for Angular 14)
- ✅ Node.js v25.2.1 (system)
- ✅ npm v11.7.0
- ✅ OpenJDK 17 (for Spring Boot)
- ✅ OpenJDK 25.0.1 (system)
- ✅ Maven 3.9.12
- ✅ Docker + Docker Compose

## 📝 Steps to Start the Project

### 1. **IMPORTANT** - Enable Docker

You must **log out and log back in** to your session for Docker to work without sudo.

After reconnecting, verify with:

```bash
docker ps
```

### 2. Start the MySQL Database

```bash
# At the project root
docker compose up -d
```

This will:

- Download the MySQL 8.0 image
- Create the `yoga` database
- Automatically execute the `ressources/sql/script.sql` script
- Expose MySQL on port 3306

To verify that MySQL is running:

```bash
docker compose ps
docker compose logs mysql
```

### 3. Start the Backend (Spring Boot)

**Option 1: Using the script**
```bash
./start-back.sh
```

**Option 2: Manually**
```bash
cd back
mvn spring-boot:run
```

The backend will be accessible at: `http://localhost:8080`

### 4. Start the Frontend (Angular)

**⚠️ IMPORTANT: The frontend must be launched with Node 16 and proxy enabled!**

**Option 1: Using the script (recommended)**
```bash
./start-front.sh
```

**Option 2: Manually**
```bash
cd front
nvm use 16
npm run start -- --proxy-config src/proxy.config.json
```

The frontend will be accessible at: `http://localhost:4200`

> **Note**: The proxy (`src/proxy.config.json`) automatically redirects `/api/*` calls to the backend on port 8080.

## ⚠️ Warning: Stop Services Before Restarting

If you get an "Address already in use" / "Port already in use" error:

### Check What's Running on Ports

```bash
# See all listening ports with processes
sudo ss -tulpn | grep -E ':(3306|8080|4200)'

# See what's using a specific port
lsof -i :8080    # Backend
lsof -i :4200    # Frontend
lsof -i :3306    # MySQL

# See all Java and Node processes
ps aux | grep -E "(java|node)" | grep -v grep
```

### Stop Services

```bash
# Stop the backend (port 8080)
pkill -f "spring-boot:run"

# Or kill a specific process by PID
lsof -i :8080  # note the PID in column 2
kill <PID>

# Stop the frontend (port 4200)
pkill -f "ng serve"

# Stop MySQL
docker compose down
```

### Complete Example to Identify and Kill

```bash
# 1. Identify the process on port 8080
lsof -i :8080
# Result: java  12345  max  ...

# 2. Kill the process
kill 12345

# Or in one command
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
docker compose exec mysql mysql -u root -proot yoga

# Complete reset (deletes data)
docker compose down -v
docker compose up -d
```

### Backend

```bash
cd back

# Compile
mvn clean install

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

### Frontend

**⚠️ IMPORTANT: Use Node 16 for frontend commands**

```bash
cd front

# Ensure Node 16 is being used
nvm use 16

# Install dependencies (already done)
npm install

# Run in dev mode with proxy
npm run start -- --proxy-config src/proxy.config.json

# Run Jest unit tests
npm run test                    # Without coverage
npm test -- --coverage          # With coverage

# Run e2e tests with Cypress (requires backend + frontend running)
npm run cypress:run             # Headless mode
npm run cypress:open            # Interactive mode
npm run e2e:coverage            # Generate coverage report after tests
```

**💡 Tip**: Use the scripts at the project root (./start-back.sh, ./run-jest.sh, etc.) which automatically manage Java/Node versions!

## 🔧 Configuration

### Database

- **Host**: localhost
- **Port**: 3306
- **Database**: yoga
- **User**: root
- **Password**: root

Configuration in: `back/src/main/resources/application.properties`

### Default User

- **Email**: yoga@studio.com
- **Password**: test!1234

## 🧪 Tests

### Unit Tests (Jest)

Jest tests work with **Node 16** and automatically generate a **coverage** report.

**Option 1: Using the script (recommended - generates coverage)**
```bash
./run-jest.sh
```
✅ Generates coverage report in `front/coverage/jest/lcov-report/index.html`

**Option 2: Manually**
```bash
cd front
nvm use 16
npm run test              # Without coverage
npm test -- --coverage    # With coverage
```

### E2E Tests (Cypress)

Cypress tests require that **both backend AND frontend** are running.

**Step 1: Start services**
```bash
# Terminal 1: Backend
./start-back.sh

# Terminal 2: Frontend  
./start-front.sh
```

**Step 2: Run tests**

**Option 1: Using the script (recommended - generates coverage)**
```bash
./run-cypress.sh
```

The script:
- ✅ Verifies services are running (ports 8080 and 4200)
- ✅ Launches Cypress tests in headless mode
- ✅ Automatically generates coverage report
- ✅ Exits cleanly at the end

**Option 2: Manually**
```bash
cd front
nvm use 16
npm run cypress:run       # Headless mode
# or
npm run cypress:open      # Interactive mode (no auto coverage)
npm run e2e:coverage      # Generate coverage after tests
```

### 📊 View Coverage Reports

```bash
./view-coverage.sh
```

This will display paths to:
- **Jest**: `front/coverage/jest/lcov-report/index.html`
- **Cypress**: `front/coverage/lcov-report/index.html`

Open these files in your browser to see detailed code coverage reports.

## 📁 Project Structure

```
savasana/
├── back/          # Spring Boot Backend
├── front/         # Angular Frontend
├── ressources/    # SQL Scripts and Postman
└── docker-compose.yml  # MySQL Configuration
```

## 🐛 Troubleshooting

### Docker Doesn't Work

1. Verify you have logged out and back in
2. Check: `groups` (must contain "docker")
3. If needed, restart the service: `sudo systemctl restart docker`

### MySQL Won't Start

```bash
# View logs
docker compose logs mysql

# Reset
docker compose down -v
docker compose up -d
```

### Port Already in Use

```bash
# Check used ports
sudo ss -tulpn | grep -E ':(3306|8080|4200)'

# Stop the service
pkill -f "spring-boot:run"    # Backend
pkill -f "ng serve"           # Frontend
docker compose down           # MySQL
```

## 🔑 Environment Variables

### Backend (Java 17)

Automatically configured by `start-back.sh`:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### Frontend (Node 16)

Automatically configured by scripts using:

```bash
export PATH="$HOME/.config/nvm/versions/node/v16.20.2/bin:$PATH"
```

## 📦 Dependencies

### Backend

- **Spring Boot**: 2.6.1
- **Java**: 17
- **Lombok**: 1.18.30
- **MapStruct**: 1.5.5.Final
- **MySQL Connector**: 8.0.27
- **JWT**: 0.9.1

### Frontend

- **Angular**: 14.2.0
- **Node**: 16.20.2
- **RxJS**: 7.5.6
- **Angular Material**: 14.2.0
- **Cypress**: 10.4.0
- **Jest**: 28.1.3

## 🚀 Quick Start Summary

```bash
# 1. Start MySQL
docker compose up -d

# 2. Start Backend (Terminal 1)
./start-back.sh

# 3. Start Frontend (Terminal 2)
./start-front.sh

# 4. Run Tests
./run-jest.sh       # Unit tests with coverage
./run-cypress.sh    # E2E tests with coverage (requires services running)

# 5. View Coverage
./view-coverage.sh
```

## 📖 Additional Resources

- **Postman Collection**: `ressources/postman/yoga.postman_collection.json`
- **SQL Script**: `ressources/sql/script.sql`
- **French Documentation**: `SETUP.md`
