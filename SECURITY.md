# Dokumentacja Bezpieczeństwa

## JWT Token Security

### Koncepcja

JWT (JSON Web Token) służy do bezpiecznej transmisji informacji o użytkowniku między serwisami.

**Struktura JWT**: `header.payload.signature`

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### Token Validation Flow

```
1. Żądanie przychodzi z Authorization header
   Authorization: Bearer <token>

2. JwtAuthenticationFilter przechwytuje
   
3. Wyodrębnia token (usuwa "Bearer ")

4. Wysyła do auth-service via gRPC
   TokenValidationRequest { token }

5. Auth-service weryfikuje:
   ✓ Podpis (HMAC-SHA256)
   ✓ Wygaśnięcie
   ✓ Format

6. Zwraca odpowiedź:
   TokenValidationResponse {
     valid: true/false,
     userId: "...",
     username: "...",
     roles: [...]
   }

7. Jeśli valid=false → HTTP 401 Unauthorized
   Jeśli valid=true  → Żądanie przechodzi dalej
```

## Konfiguracja Bezpieczeństwa

### 1. JWT Secret

**Produkcja**:
```bash
# Generowanie bezpiecznego secretu (min. 256 bitów)
openssl rand -base64 32

export JWT_SECRET="your-very-long-random-secret-key-minimum-32-characters"
```

**Nie rób**:
❌ Nie używaj domyślnego secretu
❌ Nie hardcoduj secretu w kodzie
❌ Nie używaj słabych secretów

### 2. JWT Expiration

```bash
# Rekomendowane czasy wygaśnięcia:

# Krótkoterminowe (mobil apps, single-page apps)
JWT_EXPIRATION=900000      # 15 minut

# Standardowe (web apps)
JWT_EXPIRATION=3600000     # 1 godzina

# Długoterminowe (backend-to-backend)
JWT_EXPIRATION=86400000    # 24 godziny

# Bardzo długoterminowe (refresh tokens)
JWT_EXPIRATION=2592000000  # 30 dni
```

### 3. HTTPS/TLS

**W produkcji zawsze używaj HTTPS**:

```yaml
# Konfiguracja Micronaut
micronaut:
  server:
    port: 443  # HTTPS
    ssl:
      enabled: true
      key-store:
        path: "classpath:keystore.jks"
        password: "your-keystore-password"
        type: PKCS12
```

### 4. CORS (Cross-Origin Resource Sharing)

Istniejący `GlobalCorsFilter` w api-server:

```java
// Sprawdzić dozwolone origins:
// - localhost (development)
// - https://yourdomain.com (production)
```

Aktualizacja dla produkcji:

```java
response.header("Access-Control-Allow-Origin", "https://yourdomain.com");
response.header("Access-Control-Allow-Credentials", "true");
response.header("Access-Control-Max-Age", "3600");
```

## Wdrażanie w Produkcji

### 1. Zmienne Środowiskowe

```bash
# Security
export JWT_SECRET="$(openssl rand -base64 32)"
export JWT_EXPIRATION=3600000

# Database
export DATASOURCES_DEFAULT_URL="jdbc:postgresql://prod-db:5432/investment_portfolio"
export DATASOURCES_DEFAULT_USERNAME="db_user"
export DATASOURCES_DEFAULT_PASSWORD="$(openssl rand -base64 32)"

# Message Broker
export KAFKA_BOOTSTRAP_SERVERS="kafka-1:9092,kafka-2:9092,kafka-3:9092"

# Cache
export REDIS_URI="redis://prod-redis:6379"

# Microservices
export AUTH_SERVICE_HOST="auth-service.prod.svc.cluster.local"
export AUTH_SERVICE_PORT=50050
export PORTFOLIO_SERVICE_HOST="portfolio-service.prod.svc.cluster.local"
export PORTFOLIO_SERVICE_PORT=50051
```

### 2. Docker Security

```dockerfile
# Uruchamiaj jako non-root user
RUN useradd -m -u 1000 appuser
USER appuser

# Nie eksperymentuj z sekretem
ENV JWT_SECRET=${JWT_SECRET}
```

### 3. Network Security

```yaml
# docker-compose.yml
networks:
  portfolio-network:
    driver: bridge
    
services:
  auth-service:
    networks:
      - portfolio-network  # Tylko wewnętrzna komunikacja
```

## Endpoints & Access Control

### Public Endpoints (brak autentykacji)

```
POST   /v1/api/auth/login         - Logowanie
POST   /v1/api/auth/register      - Rejestracja
POST   /v1/api/auth/refresh       - Odświeżenie tokenu
GET    /v1/api/health             - Health check
```

### Protected Endpoints (wymagają JWT)

```
GET    /v1/api/portfolio/{id}     - Pobranie portfolio
GET    /v1/api/portfolio/{id}/history - Historia portfolio
POST   /v1/api/transaction        - Nowa transakcja
GET    /v1/api/transaction/{id}   - Szczegóły transakcji
```

## Bezpieczeństwo bazy danych

### PostgreSQL

```bash
# Strong password
export POSTGRES_PASSWORD="$(openssl rand -base64 32)"

# Użytkownik z ograniczonymi uprawnieniami
CREATE USER app_user WITH PASSWORD 'strong-password';
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO app_user;

# Szyfrowanie połączeń
# W produkcji wymuszać SSL
```

### Connection String

```
# Развитие
jdbc:postgresql://localhost:5432/investment_portfolio

# Produkcja (z SSL)
jdbc:postgresql://prod-db:5432/investment_portfolio?ssl=true&sslmode=require
```

## Redis Security

```bash
# Włączyć requirepass
CONFIG SET requirepass "your-redis-password"

# Connection string
redis://user:password@host:6379/0

# W Docker
redis-server --requirepass "secure-password"
```

## Kafka Security

### SASL/SSL Configuration

```yaml
# docker-compose.yml
kafka:
  environment:
    KAFKA_SECURITY_PROTOCOL: SASL_SSL
    KAFKA_SASL_MECHANISM: PLAIN
    KAFKA_SASL_JAAS_CONFIG: |
      org.apache.kafka.common.security.plain.PlainLoginModule required \
      username="kafka-user" \
      password="kafka-password";
```

## Token Refresh Strategy

### Refresh Token Flow

```
1. Użytkownik loguje się → otrzymuje JWT + Refresh Token

2. JWT wygasa po 1 godzinie

3. Frontend wysyła Refresh Token
   POST /v1/api/auth/refresh
   { "refreshToken": "..." }

4. Auth-service weryfikuje Refresh Token

5. Zwraca nowy JWT (bez Refresh Token'a)
```

## Monitoring & Alerting

### Ataki do monitorowania

1. **Brute Force na logowanie**
   - Limit żądań: max 5 prób/5 minut
   - Blokada IP po 10 nieudanych próbach

2. **Token tampering**
   - Alert gdy podpis JWT invalid
   - Log każdej nieudanej walidacji

3. **Expired tokens**
   - Monitor użycia wygasłych tokenów
   - Alert na anomalie

### Logging Security Events

```java
log.warn("Failed authentication attempt from IP: {} for user: {}", 
    request.getRemoteAddress(), username);
log.info("Token validation failed: {}", validationResponse.getError());
log.error("Potential attack - multiple failed attempts from IP: {}", ip);
```

## Checklist Bezpieczeństwa

### Przed deployem do produkcji

- [ ] JWT_SECRET zmieniony na bezpieczny (min. 32 znaki)
- [ ] JWT_EXPIRATION ustawiony na rozsądną wartość (1-2h)
- [ ] HTTPS/TLS włączony
- [ ] CORS dozwolone tylko dla znanych origins
- [ ] Database hasła zmienione
- [ ] Redis requirepass ustawiony
- [ ] Kafka zabezpieczony SASL/SSL
- [ ] Non-root user w Docker containers
- [ ] Network security group skonfigurowany
- [ ] Backup strategia dla bazy danych
- [ ] Monitoring i alerting aktywny
- [ ] Logi centralizowane (Elasticsearch/Splunk)
- [ ] Rate limiting włączony
- [ ] IP whitelist'a skonfigurowana dla production

### Ciągłe audyty

- [ ] Przegląd logów bezpieczeństwa (codziennie)
- [ ] Rotacja JWT_SECRET (co 90 dni)
- [ ] Aktualizacja zależności (comiesięcznie)
- [ ] Penetration testing (co kwartał)

## Incident Response

### Token Compromise

```bash
# 1. Zmień JWT_SECRET
export JWT_SECRET="$(openssl rand -base64 32)"

# 2. Restartuj auth-service
docker-compose restart auth-service

# 3. Anuluj wszystkie active sessions
# (wymaga implementacji token blacklist)

# 4. Wyślij notyfikację do użytkowników
```

### Database Breach

```bash
# 1. Anuluj dostęp
docker-compose down

# 2. Zmień wszystkie hasła
export POSTGRES_PASSWORD="..."
export REDIS_PASSWORD="..."

# 3. Przywróć z backupu
restore_from_backup()

# 4. Zmień JWT_SECRET
export JWT_SECRET="..."
```

## Referencje

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc7519)
- [gRPC Security](https://grpc.io/docs/guides/auth/)
- [Kubernetes Security Best Practices](https://kubernetes.io/docs/concepts/security/)
