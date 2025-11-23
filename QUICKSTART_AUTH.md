# Quick Start - Auth Service & JWT

## Szybki Start

### 1. Uruchomienie systemu

```bash
# Skopiuj przykładowe zmienne środowiskowe
cp .env.example .env

# Uruchom wszystkie serwisy (w tym auth-service)
docker-compose up -d

# Sprawdź status
docker-compose ps
```

### 2. Logowanie

```bash
# Zaloguj się
curl -X POST http://localhost:8000/v1/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123"
  }'

# Odpowiedź:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "type": "Bearer",
#   "expiresIn": 3600
# }
```

### 3. Użyj tokenu w żądaniach

```bash
# Zapisz token
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Wykonaj żądanie z tokenem
curl -X GET http://localhost:8000/v1/api/portfolio/1 \
  -H "Authorization: Bearer $TOKEN"

# Odpowiedź:
# {
#   "id": 1,
#   "name": "My Portfolio",
#   "items": [...]
# }
```

### 4. Odświeżenie tokenu (po wygaśnięciu)

```bash
curl -X POST http://localhost:8000/v1/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "your-refresh-token"
  }'
```

## Struktura JWT

Dekoduj token na [jwt.io](https://jwt.io):

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user-id-123",
    "username": "user@example.com",
    "roles": ["ROLE_USER"],
    "token_type": "Bearer",
    "iat": 1700000000,
    "exp": 1700003600
  },
  "signature": "HMAC-SHA256(header.payload, secret)"
}
```

## Zmienne Środowiskowe

### Development

```bash
# .env
JWT_SECRET=my-secret-key-for-development
JWT_EXPIRATION=3600000

# Serwisy
AUTH_SERVICE_HOST=localhost
AUTH_SERVICE_PORT=50050

PORTFOLIO_SERVICE_HOST=localhost
PORTFOLIO_SERVICE_PORT=50051

TRANSACTION_SERVICE_HOST=localhost
TRANSACTION_SERVICE_PORT=50052

MARKET_DATA_SERVICE_HOST=localhost
MARKET_DATA_SERVICE_PORT=50053

# Database
DATASOURCES_DEFAULT_URL=jdbc:postgresql://localhost:5432/investment_portfolio
DATASOURCES_DEFAULT_USERNAME=admin
DATASOURCES_DEFAULT_PASSWORD=admin123
```

### Production

```bash
# Wygeneruj sekret
JWT_SECRET=$(openssl rand -base64 32)

# Ustaw zmienne
export JWT_SECRET="$JWT_SECRET"
export JWT_EXPIRATION=3600000

# Inne serwisy (adresy wewnętrzne)
export AUTH_SERVICE_HOST=auth-service.prod.svc.cluster.local
export PORTFOLIO_SERVICE_HOST=portfolio-service.prod.svc.cluster.local
```

## Architektura Auth Flow

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ 1. Login
       │ POST /auth/login
       │ {username, password}
       ▼
┌──────────────────┐         ┌─────────────┐
│   API Server     │─────────→ Auth Service│
│  (api-server)    │ 2. Validate Credentials
└────────┬─────────┘         └─────────────┘
         │ 3. Return JWT
         │ {token, expiresIn}
         │
       ┌─▼────────────┐
       │   Client     │
       │ Stores JWT   │
       └──┬───────────┘
         │ 4. Next Request
         │ GET /portfolio/1
         │ Authorization: Bearer {token}
         ▼
┌──────────────────┐         ┌─────────────┐
│   API Server     │─────────→ Auth Service│
│  (api-server)    │ 5. Validate Token
└────────┬─────────┘         └─────────────┘
         │ 6. Valid ✓
         │
         ▼
┌──────────────────────┐
│ Portfolio Service    │
│ Process Request      │
└──────────┬───────────┘
           │
           ▼
        Database
```

## Endpoints API

### Public (bez JWT)

```
POST   /v1/api/auth/login
POST   /v1/api/auth/register
POST   /v1/api/auth/refresh
GET    /v1/api/health
```

### Protected (z JWT w Authorization header)

```
GET    /v1/api/portfolio/{id}
GET    /v1/api/portfolio/{id}/history
GET    /v1/api/portfolio/{id}/history/summary

POST   /v1/api/transaction
GET    /v1/api/transaction/{id}
GET    /v1/api/transaction/portfolio/{portfolioId}

GET    /v1/api/market-data/{ticker}
GET    /v1/api/market-data/search
```

## Testowanie

### 1. Test Health Check (public endpoint)

```bash
curl http://localhost:8000/v1/api/health
```

### 2. Test Login

```bash
curl -X POST http://localhost:8000/v1/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@example.com",
    "password": "admin123"
  }' | jq '.'
```

### 3. Test Protected Endpoint (bez tokenu - powinno zwrócić 401)

```bash
curl -i http://localhost:8000/v1/api/portfolio/1
# Response: 401 Unauthorized - Missing Authorization header
```

### 4. Test Protected Endpoint (z tokenem)

```bash
# Uzyskaj token
TOKEN=$(curl -s -X POST http://localhost:8000/v1/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@example.com",
    "password": "admin123"
  }' | jq -r '.token')

# Użyj tokenu
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/v1/api/portfolio/1
```

### 5. Test Invalid Token

```bash
curl -i -H "Authorization: Bearer invalid-token" \
  http://localhost:8000/v1/api/portfolio/1
# Response: 401 Unauthorized - Invalid JWT token
```

## Debugging

### Logi Auth Service

```bash
# Wyświetl logi w realtime
docker-compose logs -f auth-service

# Szukaj błędów
docker-compose logs auth-service | grep ERROR

# Szukaj walidacji tokenów
docker-compose logs auth-service | grep "Token validation"
```

### Logi API Server

```bash
# Logi filtra autentykacji
docker-compose logs api-server | grep "JwtAuthenticationFilter"

# Wszystkie logi
docker-compose logs -f api-server
```

### Dekoduj JWT

Online: https://jwt.io

Lub CLI:
```bash
# Zainstaluj jwt-cli
npm install -g jwt-cli

# Dekoduj
jwt-cli decode "your-token-here"

# Weryfikuj
jwt-cli verify "your-token-here" "your-secret-key"
```

## Problemy i Rozwiązania

### Problem: "Missing Authorization header"

**Rozwiązanie**: Dodaj header `Authorization: Bearer <token>`

```bash
# ❌ Bez header
curl http://localhost:8000/v1/api/portfolio/1

# ✅ Z header
curl -H "Authorization: Bearer $TOKEN" http://localhost:8000/v1/api/portfolio/1
```

### Problem: "Invalid JWT token"

**Rozwiązania**:
- Sprawdź czy token nie wygasł
- Sprawdź czy sekret JWT jest taki sam w auth-service
- Sprawdź czy token jest prawidłowo skopiowany (bez spacji)

```bash
# Sprawdź wygaśnięcie
echo $TOKEN | jwt-cli decode

# Porównaj sekrety
docker-compose exec auth-service env | grep JWT_SECRET
```

### Problem: Auth-service nie odpowiada

**Sprawdzenie**:
```bash
# Czy auth-service jest uruchomiony?
docker-compose ps | grep auth-service

# Czy nasłuchuje na porcie 50050?
docker-compose exec auth-service netstat -tlnp | grep 50050

# Czy jest dostęp sieciowy?
docker-compose exec api-server telnet auth-service 50050
```

## Następne kroki

1. **Zabezpieczenie produkcji** → zobacz [SECURITY.md](SECURITY.md)
2. **Architektura systemu** → zobacz [ARCHITECTURE.md](ARCHITECTURE.md)
3. **Szczegóły Auth Service** → zobacz [auth-service/README.md](auth-service/README.md)
4. **Implementacja własnych endpoints** → zobacz dokumentację Micronaut

## Przydatne linki

- [JWT Documentation](https://jwt.io/introduction)
- [Micronaut Security Guide](https://micronaut-projects.github.io/micronaut-security/latest/guide/)
- [gRPC Documentation](https://grpc.io/docs/)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
