# Auth Service - Podsumowanie Implementacji

## Co zostało dodane

### 1. Nowa Mikrousługa: Auth Service

**Lokalizacja**: `D:/my-investment-portfolio/auth-service`

#### Struktura katalogów
```
auth-service/
├── src/main/java/com/sood/
│   ├── Application.java
│   └── auth/
│       ├── api/
│       │   └── AuthServiceGrpc.java          # gRPC endpoint
│       ├── jwt/
│       │   └── JwtTokenProvider.java         # JWT operations
│       └── service/
│           └── AuthService.java              # Business logic
├── src/main/resources/
│   └── application.yml                       # Configuration
├── pom.xml                                   # Maven dependencies
├── Dockerfile                                # Container image
└── README.md                                 # Service documentation
```

#### Komponenty
- **JwtTokenProvider**: Walidacja, ekstrakcja claims, weryfikacja podpisu
- **AuthService**: Logika biznesowa walidacji
- **AuthServiceGrpc**: Endpoint gRPC
- **application.yml**: Konfiguracja (porty, JWT secret)

### 2. Aktualizacje API Server

**Lokalizacja**: `D:/my-investment-portfolio/api-server`

#### Nowe pliki
```
api-server/src/main/java/com/sood/auth/
├── filter/
│   ├── JwtAuthenticationFilter.java    # HTTP filter do walidacji JWT
│   └── AuthExclusionList.java          # Wyłączone ścieżki
└── grpc/
    └── AuthGrpcClient.java             # gRPC client do auth-service
```

#### Zmiany w pom.xml
- Dodano JJWT (JSON Web Token) zależności:
  - `jjwt-api`
  - `jjwt-impl`
  - `jjwt-jackson`

#### Funkcjonalność
- HTTP filter przechwytuje wszystkie żądania `/v1/api/**`
- Wyodrębnia JWT z nagłówka `Authorization: Bearer <token>`
- Wysyła do auth-service w celu walidacji
- Zwraca 401 jeśli token niepoprawny
- Dodaje info użytkownika do atrybutów żądania

### 3. Definicje Proto

**Lokalizacja**: `D:/my-investment-portfolio/api-model/src/main/proto/auth.proto`

```proto
service AuthService {
  rpc ValidateToken(TokenValidationRequest) returns (TokenValidationResponse);
}

message TokenValidationRequest {
  string token = 1;
}

message TokenValidationResponse {
  bool valid = 1;
  string error = 2;
  string user_id = 3;
  string username = 4;
  repeated string roles = 5;
  string token_type = 6;
}
```

### 4. Docker Compose

**Lokalizacja**: `D:/my-investment-portfolio/docker-compose.yml` (NOWY)

Konsoliduje wszystkie serwisy:
- `auth-service` (port 8082, gRPC 50050)
- `api-server` (port 8000)
- `portfolio-service` (port 8080, gRPC 50051)
- `transaction-service` (port 8081, gRPC 50052)
- `market-data-service` (port 8083, gRPC 50053)
- Infrastruktura: PostgreSQL, Kafka, Redis, RabbitMQ

### 5. Dokumentacja

#### Nowe pliki dokumentacji
- **ARCHITECTURE.md** - Architektura systemu z auth layer
- **SECURITY.md** - Best practices bezpieczeństwa, JWT, konfiguracja produkcji
- **QUICKSTART_AUTH.md** - Tutorial dla developers
- **auth-service/README.md** - Dokumentacja auth-service
- **IMPLEMENTATION_SUMMARY.md** - Ten plik

## Kompletny Auth Flow

### 1. Rejestracja
```
POST /v1/api/auth/register
{
  "username": "user@example.com",
  "email": "user@example.com",
  "password": "password123"
}

AuthService:
1. Sprawdza czy username/email już istnieje
2. Enkoduje hasło za pomocą BCrypt
3. Tworzy UserEntity
4. Zapisuje do bazy danych

Response:
{
  "userId": "1",
  "message": "Registration successful",
  "success": true
}
```

### 2. Logowanie
```
POST /v1/api/auth/login
{
  "username": "user@example.com",
  "password": "password123"
}

AuthService:
1. Szuka użytkownika w bazie
2. Sprawdza czy konto jest enabled
3. Weryfikuje hasło (BCrypt)
4. Generuje JWT token (JwtTokenGenerator)
5. Aktualizuje lastLoginAt
6. Zwraca token

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful",
  "success": true
}
```

### 3. Autoryzacja Żądań
```
GET /v1/api/portfolio/1
Authorization: Bearer <token>

Przepływ:
1. JwtAuthenticationFilter przechwytuje żądanie
2. Wyodrębnia token z Authorization header
3. Wysyła do auth-service via gRPC (ValidateToken)
4. Auth Service:
   - Waliduje podpis JWT
   - Sprawdza wygaśnięcie
   - Ekstrakcja userId, username, roles
5. Jeśli valid → żądanie idzie dalej z info użytkownika
6. Jeśli invalid → HTTP 401 Unauthorized

Response (jeśli token valid):
{
  "id": 1,
  "name": "Portfolio Name",
  "items": [...]
}
```

### 4. Architektura Bazy Danych
```
PostgreSQL (investment_portfolio)
│
└─ Table: users
   ├── id (Long, PK)
   ├── username (String, UNIQUE)
   ├── email (String, UNIQUE)
   ├── password (String, BCrypt hashed)
   ├── role (String, e.g. "ROLE_USER")
   ├── enabled (Boolean)
   ├── created_at (LocalDateTime)
   └── last_login_at (LocalDateTime)
```

## Komunikacja Międzyusługowa

### gRPC
- Auth Service ← API Server (walidacja JWT)
- Portfolio Service ← API Server (dane portfolio)
- Transaction Service ← API Server (transakcje)
- Market Data Service ← API Server (ceny rynkowe)

### Kafka
- Transaction events (asynchronicznie)
- Portfolio updates

## Bezpieczeństwo

### Hasła
- **Encoding**: BCrypt (org.mindrot.jbcrypt.BCrypt)
- **Cost Factor**: 12 (bezpieczne, ale wolne)
- **Storage**: Hashed hasła w bazie, nigdy plain-text

### JWT Configuration
- **Algorithm**: HS256 (HMAC with SHA-256)
- **Secret**: `JWT_SECRET` (env variable, min. 32 znaki)
- **Expiration**: `JWT_EXPIRATION` (default: 3600s = 1h)
- **Claims**: sub (userId), username, roles, token_type, iat, exp

### Protected Endpoints
Wszystkie `/v1/api/**` wymagają JWT (za wyjątkiem wyłączonych ścieżek)

### Excluded Endpoints (Public)
- POST `/v1/api/auth/login`
- POST `/v1/api/auth/register`
- POST `/v1/api/auth/refresh`
- GET `/v1/api/health`

### Walidacja
1. Username/Email nie mogą być duplikaty
2. Hasła muszą być min. 6 znaków (rekomendacja: enforceować w UI/API)
3. Konta mogą być disabled
4. Tokeny wygasają po określonym czasie

## Porty

```
Port 8000   - API Server (REST Gateway)
Port 8082   - Auth Service (REST)
Port 8080   - Portfolio Service (REST fallback)
Port 8081   - Transaction Service (REST fallback)
Port 8083   - Market Data Service (REST fallback)

Port 50050  - Auth Service (gRPC)
Port 50051  - Portfolio Service (gRPC)
Port 50052  - Transaction Service (gRPC)
Port 50053  - Market Data Service (gRPC)

Port 5432   - PostgreSQL
Port 9092   - Kafka
Port 6379   - Redis
Port 5672   - RabbitMQ AMQP
Port 15672  - RabbitMQ Management UI
Port 2181   - Zookeeper
```

## Klasy Javaowe

### Entity (JPA/Hibernate)
- **UserEntity** - Model użytkownika z polami: id, username, email, password (hashed), role, enabled, createdAt, lastLoginAt

### Repository (Data Access)
- **UserRepository** - CRUD operations dla użytkowników
  - `findByUsername(String)` - szukanie po username
  - `findByEmail(String)` - szukanie po email
  - `existsByUsername(String)` - sprawdzenie czy istnieje
  - `existsByEmail(String)` - sprawdzenie czy istnieje

### Services
- **AuthService** - Główna logika
  - `login(LoginRequest)` - Weryfikacja credentials, generowanie JWT
  - `register(RegisterRequest)` - Tworzenie nowego użytkownika
  - `validateToken(String)` - Walidacja JWT (dla gRPC)
  - `refreshTokenIfValid(String)` - Odświeżenie tokenu

### JWT Services
- **JwtTokenProvider** - Walidacja tokenów
  - `validateToken(String)` - Sprawdzenie podpisu
  - `extractClaims(String)` - Pobranie claims
  - `extractUserId(String)` - Pobranie userId
  - `isTokenExpired(String)` - Sprawdzenie wygaśnięcia

- **JwtTokenGenerator** - Generowanie tokenów
  - `generateToken(userId, username, roles)` - Tworzenie JWT

### Security
- **PasswordEncoder** - Enkodowanie haseł
  - `encode(String)` - Hashowanie hasła (BCrypt)
  - `matches(String, String)` - Weryfikacja hasła

### Controllers
- **AuthController** (REST)
  - `POST /v1/api/auth/login` - Logowanie
  - `POST /v1/api/auth/register` - Rejestracja

- **AuthServiceGrpc** (gRPC)
  - `validateToken(TokenValidationRequest)` - Walidacja dla inne serwisów

### DTOs
- **LoginRequest** - username, password
- **LoginResponse** - token, message, success
- **RegisterRequest** - username, email, password
- **RegisterResponse** - userId, message, success

## Zmienne Środowiskowe

### Minimalna konfiguracja (development)
```bash
JWT_SECRET=my-secret-key-minimum-32-characters-long
JWT_EXPIRATION=3600000
DATASOURCES_DEFAULT_URL=jdbc:postgresql://localhost:5432/investment_portfolio
DATASOURCES_DEFAULT_USERNAME=admin
DATASOURCES_DEFAULT_PASSWORD=admin123
```

### Production
```bash
JWT_SECRET=$(openssl rand -base64 32)  # Bezpieczny sekret (min 32 znaki)
JWT_EXPIRATION=3600000                 # 1 godzina
DATASOURCES_DEFAULT_URL=jdbc:postgresql://prod-db:5432/investment_portfolio
DATASOURCES_DEFAULT_USERNAME=secure_user
DATASOURCES_DEFAULT_PASSWORD=$(openssl rand -base64 32)
```

## Instalacja i Wdrażanie

### Development
```bash
cd /path/to/project
docker-compose up -d
```

### Build
```bash
mvn clean package
```

### Docker Build
```bash
docker build -t auth-service:latest ./auth-service
docker build -t api-server:latest ./api-server
```

## Testing

### Test Login
```bash
curl -X POST http://localhost:8000/v1/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password123"}'
```

### Test Protected Endpoint
```bash
TOKEN="your-jwt-token-here"
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/v1/api/portfolio/1
```

### Health Check
```bash
curl http://localhost:8000/v1/api/health
```

## Modyfikacje w Istniejących Serwisach

### api-server/pom.xml
- Dodano JJWT zależności
- Nie zmieniono istniejącego kodu gRPC

### Rodzic pom.xml
- Dodano moduł `auth-service`

## Pliki utworzone

### Auth Service
```
auth-service/
├── pom.xml (z zależnościami: BCrypt, Hibernate/JPA, PostgreSQL)
├── Dockerfile
├── README.md
└── src/main/java/com/sood/auth/
    ├── Application.java
    ├── api/
    │   ├── AuthServiceGrpc.java        # gRPC endpoint (walidacja JWT)
    │   └── AuthController.java         # REST endpoints (login/register)
    ├── dto/
    │   ├── LoginRequest.java
    │   ├── LoginResponse.java
    │   ├── RegisterRequest.java
    │   └── RegisterResponse.java
    ├── infrastructure/
    │   ├── entity/
    │   │   └── UserEntity.java         # User model
    │   └── repository/
    │       └── UserRepository.java     # User DAO
    ├── jwt/
    │   ├── JwtTokenProvider.java       # Walidacja tokenów
    │   └── JwtTokenGenerator.java      # Generowanie tokenów
    ├── security/
    │   └── PasswordEncoder.java        # BCrypt encoding
    └── service/
        └── AuthService.java            # Business logic
└── src/main/resources/
    └── application.yml                 # Config (database, JWT)
```

### API Server Updates
```
api-server/src/main/java/com/sood/auth/
├── filter/
│   ├── JwtAuthenticationFilter.java    # HTTP filter
│   └── AuthExclusionList.java          # Wyłączone ścieżki
└── grpc/
    └── AuthGrpcClient.java             # gRPC client
```

### Proto Definitions
```
api-model/src/main/proto/
└── auth.proto                          # gRPC service definitions
```

### Documentation
```
├── ARCHITECTURE.md                     # Full architecture
├── SECURITY.md                         # Security best practices
├── QUICKSTART_AUTH.md                  # Developer tutorial
├── IMPLEMENTATION_SUMMARY.md           # This file
└── docker-compose.yml                  # All services + infra
```

### Tests
```
auth-service/src/test/java/com/sood/auth/service/
└── AuthServiceTest.java                # Login/Register tests
```

## Następne Kroki

1. **Build**: `mvn clean package`
2. **Run**: `docker-compose up -d`
3. **Test**: Wykonaj żądania z QUICKSTART_AUTH.md
4. **Security**: Zmień JWT_SECRET w produkcji
5. **Monitor**: Sprawdzaj logi i metryki

## Dokumentacja Referencyjna

- [ARCHITECTURE.md](ARCHITECTURE.md) - Architektura
- [SECURITY.md](SECURITY.md) - Bezpieczeństwo
- [QUICKSTART_AUTH.md](QUICKSTART_AUTH.md) - Tutorial
- [auth-service/README.md](auth-service/README.md) - Service docs
