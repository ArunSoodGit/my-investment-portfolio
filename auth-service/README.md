# Auth Service

## Opis

**Auth Service** to dedykowana mikrousługa do walidacji JWT tokenów i zarządzania autoryzacją w systemie. Służy jako warstwa bezpieczeństwa dla API Gateway (api-server).

## Architektura

### Komponenty

1. **JwtTokenProvider** - Obsługuje tworzenie i walidację JWT
   - Walidacja podpisu tokenu
   - Ekstrakcja claims
   - Sprawdzanie czasu wygaśnięcia

2. **AuthService** - Logika biznesowa autoryzacji
   - Walidacja tokenów
   - Ekstrakcja danych użytkownika
   - Obsługa błędów

3. **AuthServiceGrpc** - gRPC endpoint
   - Przyjmuje żądania walidacji
   - Zwraca odpowiedź z wynikami walidacji

## Komunikacja

### gRPC Interface

```proto
service AuthService {
  rpc ValidateToken(TokenValidationRequest) returns (TokenValidationResponse);
}
```

**TokenValidationRequest:**
```proto
message TokenValidationRequest {
  string token = 1;
}
```

**TokenValidationResponse:**
```proto
message TokenValidationResponse {
  bool valid = 1;
  string error = 2;
  string user_id = 3;
  string username = 4;
  repeated string roles = 5;
  string token_type = 6;
}
```

## Uruchomienie

### Lokalnie

```bash
cd auth-service
mvn clean package
java -jar target/auth-service-0.1.jar
```

### Docker

```bash
docker build -t auth-service:latest .
docker run -e JWT_SECRET=your-secret -p 8082:8082 -p 50050:50050 auth-service:latest
```

### Docker Compose

```bash
docker-compose up -d auth-service
```

## Konfiguracja

### Zmienne środowiskowe

- `JWT_SECRET` - Sekret do podpisywania i walidacji JWT (domyślnie: `my-secret-key-for-jwt-token-generation-and-validation`)
- `JWT_EXPIRATION` - Czas wygaśnięcia tokenu w milisekundach (domyślnie: `3600000` - 1 godzina)

### Przykład konfiguracji

```yaml
jwt:
  secret: ${JWT_SECRET:my-secret-key}
  expiration: ${JWT_EXPIRATION:3600000}
```

## Integracja z API Gateway

API Gateway (api-server) używa filtra `JwtAuthenticationFilter` do:
1. Przechwycenia żądań do `/v1/api/**`
2. Wyodrębnienia tokenu z nagłówka `Authorization`
3. Wywołania auth-service do walidacji
4. Dodania informacji o użytkowniku do atrybutów żądania

### Nagłówek Authorization

```
Authorization: Bearer <jwt-token>
```

### Wyłączone ścieżki

Poniższe ścieżki nie wymagają autentykacji:
- `POST /v1/api/auth/login`
- `POST /v1/api/auth/register`
- `POST /v1/api/auth/refresh`
- `GET /v1/api/health`

## Struktura JWT

Typowy token JWT zawiera:
```json
{
  "sub": "user-id",
  "username": "username",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "token_type": "Bearer",
  "iat": 1234567890,
  "exp": 1234571490
}
```

## Porty

- **HTTP**: 8082 (logowanie, metryki)
- **gRPC**: 50050 (komunikacja międzyusługowa)

## Logowanie

Logi aplikacji zapisywane są na poziomie DEBUG dla pakietu `com.sood`.

## Testy

```bash
mvn test
```

## Zmiany potrzebne w pozostałych serwisach

### api-server

1. Zostały dodane zależności JWT
2. Został dodany filtr `JwtAuthenticationFilter`
3. GRPC client do komunikacji z auth-service

### Inne serwisy

Aby korzystać z informacji o zalogowanym użytkowniku, można je pobierać z atrybutów żądania gRPC lub zaimplementować własne filtry.
