# Savasana - Yoga Class Management Application

Full-stack application for managing yoga sessions, including:
- **Backend**: Spring Boot REST API (Java 17)
- **Frontend**: Angular 14 Application
- **Database**: MySQL 8.0

## Prerequisites

Before starting, ensure you have installed:

- **Node.js v16.x** (required for Angular 14)
- **Java 17** (OpenJDK recommended)
- **Maven 3.9+**
- **Docker & Docker Compose**
- **Git**

> **Note**: If multiple Java/Node versions are installed, the provided scripts automatically manage the correct versions.

## Installation

### 1. Clone the Project

```bash
git clone <repository-url>
cd savasana
```

### 2. Install the Database

The MySQL database is managed via Docker Compose:

```bash
docker compose up -d
```

This will:
- Download the MySQL 8.0 image
- Create the `yoga` database
- Automatically execute the `ressources/sql/script.sql` script
- Expose MySQL on port 3306

**Verify MySQL is running:**

```bash
docker compose ps
docker compose logs mysql
```

**Default configuration:**
- Host: `localhost:3306`
- Database: `yoga`
- User: `root`
- Password: `root`

### 3. Install the Backend Application

```bash
cd back
mvn clean install
```

This will compile the project and install all Maven dependencies.

### 4. Install the Frontend Application

```bash
cd front
npm install
```

> **Important**: Use Node.js v16 to avoid compatibility issues with Angular 14.

## Running the Application

### Quick Start (Recommended)

**Terminal 1 - Backend:**
```bash
./start-back.sh
```
API accessible at `http://localhost:8080`

**Terminal 2 - Frontend:**
```bash
./start-front.sh
```
Application accessible at `http://localhost:4200`

### Manual Start

**Backend:**
```bash
cd back
JAVA_HOME=/usr/lib/jvm/java-17-openjdk mvn spring-boot:run
```

**Frontend:**
```bash
cd front
nvm use 16  # If using nvm
npm run start -- --proxy-config src/proxy.config.json
```

> **Note**: The proxy (`src/proxy.config.json`) automatically redirects `/api/*` calls to the backend on port 8080.

### Default Test Account

```
Email: yoga@studio.com
Password: test!1234
```

## Running Tests

### Backend Unit Tests (JUnit)

```bash
cd back
JAVA_HOME=/usr/lib/jvm/java-17-openjdk mvn clean test
```

**Coverage statistics:**
- **77 tests** in total
- **52 integration tests** (67.5%) using `@SpringBootTest` and MockMvc
- **25 unit tests** (32.5%) using Mockito
- Coverage: **87% instructions, 66% branches**

### Frontend Unit Tests (Jest)

**With script (recommended):**
```bash
./run-jest.sh
```

**Manually:**
```bash
cd front
npm test                    # Without coverage
npm test -- --coverage      # With coverage
```

### Frontend E2E Tests (Cypress)

**Prerequisites**: Both Backend AND Frontend must be running.

**With script (recommended):**

```bash
# Terminal 1: Start the backend
./start-back.sh

# Terminal 2: Run Cypress with coverage
./run-cypress.sh
```

The `run-cypress.sh` script:
- Verifies services are running
- Launches the frontend with coverage instrumentation
- Executes Cypress tests in headless mode
- Automatically generates the coverage report

**Interactive mode (for debugging):**

```bash
# Terminal 1: Backend
./start-back.sh

# Terminal 2: Frontend with instrumentation
cd front
npm run start:e2e

# Terminal 3: Cypress interface
cd front
npm run cypress:open
```

## Generate and View Coverage Reports

### Automatic Generation

Coverage reports are automatically generated when running tests with the provided scripts:

- **Backend**: `./back/target/site/jacoco/index.html`
- **Frontend Jest**: `./front/coverage/jest/lcov-report/index.html`
- **Frontend Cypress**: `./front/coverage/lcov-report/index.html`

### View All Reports

```bash
./view-coverage.sh
```

This script displays paths to all reports and can open them in the browser.

### Manual Generation

**Backend (Jacoco):**
```bash
cd back
JAVA_HOME=/usr/lib/jvm/java-17-openjdk mvn clean test
# Report generated at: target/site/jacoco/index.html
```

**Frontend Jest:**
```bash
cd front
npm test -- --coverage
# Report generated at: coverage/jest/lcov-report/index.html
```

**Frontend Cypress:**
```bash
cd front
npm run e2e:coverage
# Report generated at: coverage/lcov-report/index.html
```

### Backend Coverage Details

The project intentionally excludes **automatically generated code** from coverage:
- DTOs (Lombok getters/setters)
- Models (JPA entities)
- Payloads (request/response classes)
- Mappers (MapStruct implementations)

This exclusion follows **industry best practices**: only code with business logic is tested.

**Result:** 100% coverage on testable code (Controllers, Services, Security).

## Useful Commands

### Docker MySQL Management

```bash
# Start MySQL
docker compose up -d

# Stop MySQL
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

# Start application
mvn spring-boot:run

# View coverage
mvn test && open target/site/jacoco/index.html
```

### Frontend

```bash
cd front

# Ensure using Node 16
nvm use 16

# Install dependencies
npm install

# Start in development mode
npm run start -- --proxy-config src/proxy.config.json

# Jest tests
npm test                    # Without coverage
npm test -- --coverage      # With coverage

# Cypress tests
npm run cypress:run         # Headless mode
npm run cypress:open        # Interactive mode
```

## Troubleshooting

### Ports Already in Use

If you get an "Address already in use" error:

```bash
# Identify process using a port
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

### Docker Not Working

```bash
# Check you are in the docker group
groups

# If docker doesn't appear, log out and log back in

# Restart Docker service
sudo systemctl restart docker

# Test
docker ps
```

### MySQL Connection Issues

```bash
# Check MySQL logs
docker compose logs mysql

# Complete database reset
docker compose down -v
docker compose up -d
```

### Backend Compilation Errors

If you have compilation errors:

```bash
# Check Java version
java -version  # Must be Java 17

# Compile with correct version
cd back
JAVA_HOME=/usr/lib/jvm/java-17-openjdk mvn clean install
```

### Frontend NPM Errors

```bash
# Check Node version
node -v  # Must be v16.x

# Reinstall dependencies
cd front
rm -rf node_modules package-lock.json
npm install
```

## Project Structure

```
savasana/
├── back/                   # Spring Boot Backend
│   ├── src/
│   │   ├── main/java/     # Source code
│   │   └── test/java/     # Tests (77 tests)
│   ├── pom.xml            # Maven configuration
│   └── target/            # Build artifacts
│       └── site/jacoco/   # Coverage report
│
├── front/                  # Angular Frontend
│   ├── src/
│   │   ├── app/           # Angular components
│   │   └── assets/        # Static resources
│   ├── cypress/           # E2E tests
│   ├── coverage/          # Coverage reports
│   ├── package.json       # npm dependencies
│   └── angular.json       # Angular configuration
│
├── ressources/
│   ├── sql/
│   │   └── script.sql     # Database initialization script
│   └── postman/
│       └── yoga.postman_collection.json
│
├── docker-compose.yml     # MySQL configuration
├── start-back.sh          # Backend start script
├── start-front.sh         # Frontend start script
├── run-jest.sh            # Jest tests script
├── run-cypress.sh         # Cypress tests script
└── view-coverage.sh       # Coverage visualization script
```

## Technologies Used

### Backend
- **Spring Boot** 2.6.1
- **Java** 17
- **Spring Security** + JWT
- **Spring Data JPA**
- **MySQL** 8.0
- **Lombok** 1.18.22
- **MapStruct** 1.5.1
- **Maven** 3.9+

### Frontend
- **Angular** 14.2.0
- **Node.js** 16.x
- **TypeScript** 4.7.4
- **Angular Material** 14.2.0
- **RxJS** 7.5.6

### Testing
- **JUnit** 5.8.1
- **Mockito** 4.0.0
- **Jest** 28.1.3
- **Cypress** 10.4.0
- **Jacoco** 0.8.11

## Test Metrics

### Backend (JUnit + Jacoco)
- **77 tests** in total
  - 52 integration tests (67.5%)
  - 25 unit tests (32.5%)
- **Coverage**
  - Instructions: 87%
  - Branches: 66%
  - 100% on non-generated code

### Frontend
- **Jest tests**: Component unit tests
- **Cypress tests**: Complete E2E tests
- Coverage reports available in `front/coverage/`

## Quick Start

```bash
# 1. Database
docker compose up -d

# 2. Backend (Terminal 1)
./start-back.sh

# 3. Frontend (Terminal 2)
./start-front.sh

# 4. Tests
./run-jest.sh       # Frontend unit tests
./run-cypress.sh    # E2E tests (requires backend + frontend)

# 5. Coverage
./view-coverage.sh
```

## Additional Resources

- **Postman Collection**: `ressources/postman/yoga.postman_collection.json`
- **SQL Script**: `ressources/sql/script.sql`
- **Detailed Setup Guide**: [SETUP.md](SETUP.md)

## License

This project is developed as part of an OpenClassrooms training program.

---

**Test account**: yoga@studio.com / test!1234
