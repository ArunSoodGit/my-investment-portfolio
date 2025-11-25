# Architektura Systemu - Investment Portfolio

## Przegląd

System opiera się na architekturze mikrousług z następującymi komponentami:

```
┌─────────────────────────────────────────────────────────────┐
│                        Klient (Frontend)                     │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│            API Server (REST Gateway) - Port 8000             │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │         JwtAuthenticationFilter                         │ │
│  │    (Walidacja JWT dla wszystkich żądań /v1/api/**)     │ │
│  └─────────────────────────────────────────────────────────┘ │
└──────┬──────────┬──────────┬──────────┬───────────────────┘
       │          │          │          │
       ▼          ▼          ▼          ▼
   ┌────────┐┌─────────┐┌──────────┐┌────────────┐
   │  Auth  ││Portfolio││Transaction││Market Data│
   │Service ││Service  ││Service    ││Service    │
   └────────┘└─────────┘└──────────┘└────────────┘
   P8082/   P8080/    P8081/      P8083/
   50050    50051     50052       50053
```

## Mikrousługi

### 1. Auth Service (NOWA)

**Cel**: Walidacja JWT tokenów i zarządzanie autoryzacją

**Porty**:
- REST: 8082
- gRPC: 50050

**Główne komponenty**:
- `JwtTokenProvider` - Walidacja i ekstrakcja claims z JWT
- `AuthService` - Logika walidacji
- `AuthServiceGrpc` - Endpoint gRPC

**Interfejs gRPC**:
```proto
service AuthService {
  rpc ValidateToken(TokenValidationRequest) returns (TokenValidationResponse);
}
```

**Zmienne środowiskowe**:
- `JWT_SECRET` - Sekret do podpisywania/walidacji
- `JWT_EXPIRATION` - Czas wygaśnięcia tokenu

### 2. API Server (Gateway)

**Cel**: Punkt wejścia dla wszystkich żądań REST

**Porty**:
- REST: 8000

**Główne komponenty**:
- `PortfolioController` - Endpoints portfolio
- `TransactionController` - Endpoints transakcji
- `JwtAuthenticationFilter` - Autentykacja
- Klienty gRPC do komunikacji z innymi serwisami

**Przepływ żądania**:
1. Żądanie przychodzi z nagłówkiem `Authorization: Bearer <token>`
2. `JwtAuthenticationFilter` przechwytuje żądanie
3. Token wysyłany do auth-service via gRPC
4. Jeśli token poprawny - żądanie idzie dalej
5. Informacja o użytkowniku dodawana do atrybutów żądania

### 3. Portfolio Service

**Cel**: Zarządzanie portfelami inwestycyjnych

**Porty**:
- REST: 8080
- gRPC: 50051

**Główne komponenty**:
- `PortfolioController` - gRPC endpoints
- `PortfolioService` - Logika biznesowa
- `PortfolioRepository` - Dostęp do bazy danych
- `ItemProcessor` - Przetwarzanie pozycji portfela

### 4. Transaction Service

**Cel**: Zarządzanie transakcjami (kupno/sprzedaż)

**Porty**:
- REST: 8081
- gRPC: 50052

**Główne komponenty**:
- `TransactionController` - gRPC endpoints
- `TransactionManager` - Logika transakcji
- `TransactionRepository` - Dostęp do bazy danych
- Publikacja zdarzeń Kafka

### 5. Market Data Service

**Cel**: Dostarczanie danych o cenach rynkowych

**Porty**:
- REST: 8083
- gRPC: 50053

**Główne komponenty**:
- `MarketDataController` - gRPC endpoints
- `CacheKeyGenerator` - Zarządzanie cache
- Integracja z Redis do cachowania

## Komunikacja Międzyusługowa

### Protokoły

- **gRPC** - Komunikacja między serwisami (efektywna, typowana)
- **Kafka** - Zdarzenia asynchroniczne (publikacja zdarzeń transakcji)
- **REST** - Interfejs dla klientów

### Przepływ danych

```
Frontend Request
    │
    ▼
API Server (Port 8000)
    │
    ├─► Auth Service (gRPC 50050) - Walidacja JWT
    │
    ├─► Portfolio Service (gRPC 50051) - Dane portfolio
    │   │
    │   ├─► PostgreSQL - Baza danych
    │   └─► Redis - Cache
    │
    ├─► Transaction Service (gRPC 50052) - Transakcje
    │   │
    │   ├─► PostgreSQL - Baza danych
    │   └─► Kafka - Publikacja zdarzeń
    │
    └─► Market Data Service (gRPC 50053) - Ceny rynkowe
        │
        └─► Redis - Cache
```

## Infrastruktura

### Bazy danych

**PostgreSQL** (Port 5432)
- Database: `investment_portfolio`
- Przechowuje: Portfele, transakcje, historię

### Message Queue

**Kafka** (Port 9092)
- Zdarzenia transakcji
- Zdarzenia aktualizacji portfolio
- Komunikacja asynchroniczna między serwisami

**RabbitMQ** (Port 5672)
- Alternatywny message broker (opcjonalnie)
- Management UI: Port 15672

### Cache

**Redis** (Port 6379)
- Cache danych rynkowych
- Cache portfeli
- Session management

## Bezpieczeństwo (Auth Flow)

### 1. Logowanie

```
POST /v1/api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 3600
}
```

### 2. Autoryzacja (dla każdego żądania)

```
GET /v1/api/portfolio/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

JWT zawiera:
{
  "sub": "user-id-123",
  "username": "user@example.com",
  "roles": ["ROLE_USER"],
  "iat": 1234567890,
  "exp": 1234571490
}
```

### 3. Walidacja

```
JwtAuthenticationFilter
    │
    ├─ Wyodrębnia token z Authorization: Bearer
    │
    └─► Auth Service (gRPC)
        │
        ├─ Waliduje podpis JWT
        ├─ Sprawdza wygaśnięcie
        ├─ Ekstrakcja claims
        │
        └─ Zwraca: valid=true/false + dane użytkownika
```

## Wyłączone ścieżki (bez autentykacji)

- `POST /v1/api/auth/login` - Logowanie
- `POST /v1/api/auth/register` - Rejestracja
- `POST /v1/api/auth/refresh` - Odświeżenie tokenu
- `GET /v1/api/health` - Health check
- `GET /v1/api/swagger-ui.html` - Dokumentacja API
- `GET /v1/api/openapi.yaml` - OpenAPI schema

## Deployment

### Docker Compose

```bash
docker-compose up -d
```

Uruchamia wszystkie serwisy z niezbędną infrastrukturą.

### Docker Compose - Serwisy

```yaml
services:
  postgres        # Baza danych
  kafka          # Message broker
  zookeeper      # Koordynator Kafka
  rabbitmq       # Alternative message broker
  redis          # Cache
  auth-service   # Walidacja JWT
  api-server     # Gateway
  portfolio-service
  transaction-service
  market-data-service
```

## Monitorowanie i Logowanie

### Logi

Wszystkie serwisy logują na poziomie:
- `DEBUG` - Pakiet `com.sood`
- `INFO` - Komunikaty ogólne
- `WARN` - Ostrzeżenia (np. błędy walidacji JWT)
- `ERROR` - Błędy

### Health Checks

- `GET /v1/api/health` - Status aplikacji
- `GET /actuator/health` - Micronaut health endpoint

## Performance & Skalowanie

### Caching

- Redis: Cache rynkowych, portfeli
- Micronaut: In-memory cache konfiguracji

### Asynchronizm

- gRPC streaming dla portfolio updates
- Kafka dla zdarzeń asynchronicznych
- RxJava3 dla operacji reaktywnych

### Partycjonowanie danych

- Portfele indexowane po ID użytkownika
- Transakcje partycjonowane po portfolio ID
- Dane rynkowe cachowane per ticker

## Wdrażanie do produkcji

### Wymagania

- Docker & Docker Compose
- Java 21+
- PostgreSQL 15+
- Kafka 7.5+

### Konfiguracja

```bash
export JWT_SECRET="your-production-secret-key"
export JWT_EXPIRATION="7200000"  # 2 godziny
export DATABASE_URL="jdbc:postgresql://prod-db:5432/portfolio"
export DATABASE_USER="admin"
export DATABASE_PASSWORD="secure-password"
export KAFKA_BOOTSTRAP_SERVERS="kafka-1:9092,kafka-2:9092,kafka-3:9092"
export REDIS_URI="redis://prod-redis:6379"

docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

