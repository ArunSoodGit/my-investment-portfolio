# Auth Service Integration Guide

## Implementation Summary

### Overview
Completed full integration between **API Server** and **Auth Service** for JWT-based authentication.

---

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                         Frontend                              │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
        ┌────────────────────────────┐
        │  API Server (Port 8080)    │
        │  REST Gateway              │
        └──────────┬─────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
  ┌──────────────┐  ┌─────────────────────┐
  │ REST Client  │  │ gRPC Client         │
  │ (HTTP)       │  │ (Auth Validation)   │
  └──────────────┘  └─────────────────────┘
        │                     │
        │ POST /v1/api/auth   │ ValidateToken
        │ /login              │ (gRPC 50050)
        │ /register           │
        │ /refresh            │
        │                     │
        └──────────┬──────────┘
                   │
                   ▼
    ┌──────────────────────────────┐
    │  Auth Service (Port 8082)    │
    ├──────────────────────────────┤
    │ REST Endpoints (HTTP 8082)   │
    │ + gRPC Server (gRPC 50050)   │
    │                              │
    │ - AuthRestController         │
    │   • POST /v1/api/auth/login  │
    │   • POST /v1/api/auth/register
    │   • POST /v1/api/auth/refresh│
    │                              │
    │ - AuthController (gRPC)      │
    │   • ValidateToken            │
    │                              │
    │ - AuthService (Business)     │
    │ - JwtTokenProvider           │
    │ - UserRepository             │
    │ - PasswordEncoder            │
    └──────────────────────────────┘
           │
           ▼
    ┌──────────────────┐
    │  PostgreSQL      │
    │  Users Table     │
    └──────────────────┘
```

---

## Implementation Details

### 1. **API Server Components**

#### A. DTOs (Serialization Layer)
- **LoginRequest** - username, password
- **LoginResponse** - token, type, expiresIn, message, success
- **RegisterRequest** - username, email, password
- **RegisterResponse** - userId, message, success
- **RefreshTokenRequest** - token

**Location:** `api-server/src/main/java/com/sood/auth/dto/`

#### B. ApiAuthService (Orchestration)
Maps HTTP calls to auth-service and handles responses.

```java
@Singleton
public class ApiAuthService {
    - login(LoginRequest) → LoginResponse
    - register(RegisterRequest) → RegisterResponse  
    - refreshToken(RefreshTokenRequest) → LoginResponse
}
```

**Location:** `api-server/src/main/java/com/sood/auth/service/ApiAuthService.java`

#### C. AuthRestController (REST Endpoints)
Exposes auth endpoints without authentication requirement.

```
POST /v1/api/auth/login     (excluded from auth filter)
POST /v1/api/auth/register  (excluded from auth filter)
POST /v1/api/auth/refresh   (excluded from auth filter)
```

**Location:** `api-server/src/main/java/com/sood/auth/controller/AuthRestController.java`

#### D. JwtAuthenticationFilter (Existing)
Intercepts requests, validates token via gRPC call to auth-service.

**Location:** `api-server/src/main/java/com/sood/auth/filter/JwtAuthenticationFilter.java`

---

### 2. **Auth Service Components**

#### A. DTOs
- **LoginRequest** - username, password
- **LoginResponse** - token, message, success
- **RegisterRequest** - username, email, password
- **RegisterResponse** - userId, message, success
- **RefreshTokenRequest** - token

**Location:** `auth-service/src/main/java/com/sood/auth/dto/`

#### B. AuthRestController (REST Endpoints)
Handles login, registration, and token refresh.

```
POST /v1/api/auth/login
POST /v1/api/auth/register
POST /v1/api/auth/refresh
```

**Location:** `auth-service/src/main/java/com/sood/auth/api/AuthRestController.java`

#### C. AuthController (gRPC Endpoint)
Handles token validation for api-server filter.

```
rpc ValidateToken(TokenValidationRequest) returns (TokenValidationResponse)
```

**Location:** `auth-service/src/main/java/com/sood/auth/api/AuthController.java`

#### D. AuthService (Business Logic)
Core logic for authentication operations.

**Location:** `auth-service/src/main/java/com/sood/auth/service/AuthService.java`

#### E. JWT Token Provider
Creates and validates JWT tokens.

**Location:** `auth-service/src/main/java/com/sood/auth/jwt/JwtTokenProvider.java`

---

## Configuration Files Updated

### API Server
**File:** `api-server/src/main/resources/application.yml`
```yaml
grpc:
  channels:
    auth:
      address: "localhost:50050"
      plaintext: true
```

### Auth Service
**File:** `auth-service/src/main/resources/application.yml`
```yaml
micronaut:
  application:
    name: auth-service
  server:
    port: 8082

grpc:
  server:
    port: 50050

datasources:
  default:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/investment_portfolio}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
    
jwt:
  secret: ${JWT_SECRET:my-secret-key-for-jwt-token-generation-and-validation}
  expiration: ${JWT_EXPIRATION:3600000}
```

---

## Authentication Flow

### 1. **Login Flow**

```
Client Request:
    POST /v1/api/auth/login
    Content-Type: application/json
    {
      "username": "user@example.com",
      "password": "password123"
    }

↓

API Server (AuthRestController):
    • Receives request
    • Calls ApiAuthService.login(request)
    
↓

ApiAuthService:
    • Makes HTTP call to auth-service /v1/api/auth/login
    • Maps auth-service response to api-server LoginResponse
    
↓

Auth Service (AuthRestController):
    • Receives login request
    • Calls AuthService.login(request)
    
↓

AuthService:
    • Finds user in database
    • Validates password via PasswordEncoder
    • Generates JWT via JwtTokenGenerator
    • Updates lastLoginAt
    
↓

Response (to client):
    200 OK
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "type": "Bearer",
      "expiresIn": 3600,
      "message": "Login successful",
      "success": true
    }
```

### 2. **Protected Request Flow**

```
Client Request:
    GET /v1/api/portfolio/123
    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

↓

API Server:
    JwtAuthenticationFilter intercepts request
    • Checks if path is in exclusion list (not for /portfolio)
    • Extracts token from Authorization header
    • Calls AuthGrpcClient.validateToken(token)
    
↓

Auth Service (gRPC):
    AuthController receives gRPC call
    • Calls AuthService.validateToken(token)
    
↓

AuthService:
    • Calls JwtTokenProvider.validateToken(token)
    • Checks token signature
    • Checks token expiration
    • Extracts userId, username, roles from JWT claims
    
↓

Response to filter:
    TokenValidationResponse {
      valid: true,
      userId: "user-123",
      username: "user@example.com",
      roles: ["ROLE_USER"]
    }

↓

Filter adds to request:
    request.setAttribute("userId", "user-123")
    request.setAttribute("username", "user@example.com")
    request.setAttribute("roles", ["ROLE_USER"])
    
↓

Chain proceeds to PortfolioController
    (Controller can access user info from request attributes)
```

### 3. **Registration Flow**

```
POST /v1/api/auth/register
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123"
}

↓

API Server → Auth Service (HTTP)

↓

AuthService:
    • Validates username/email not taken
    • Encodes password via BCrypt
    • Creates UserEntity
    • Saves to database
    
↓

Response:
    201 Created
    {
      "userId": "uuid-...",
      "message": "Registration successful",
      "success": true
    }
```

### 4. **Token Refresh Flow**

```
POST /v1/api/auth/refresh
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

↓

API Server → Auth Service (HTTP)

↓

Auth Service:
    • Validates token via JwtTokenProvider
    • Checks if expired
    • Returns validation result
    
↓

Response:
    200 OK (if valid)
    {
      "token": "same-token",
      "message": "Token is still valid",
      "success": true
    }
    
    OR
    
    401 Unauthorized (if invalid/expired)
    {
      "token": null,
      "message": "JWT token has expired",
      "success": false
    }
```

---

## Excluded Auth Paths

Routes that **do NOT require** JWT token:

```
POST   /v1/api/auth/login
POST   /v1/api/auth/register
POST   /v1/api/auth/refresh
GET    /v1/api/health
GET    /v1/api/swagger-ui.html
GET    /v1/api/openapi.yaml
```

**Configured in:** `api-server/src/main/java/com/sood/auth/filter/AuthExclusionList.java`

---

## Environment Variables

### Auth Service
```bash
JWT_SECRET=my-custom-secret-key
JWT_EXPIRATION=7200000  # 2 hours

DB_URL=jdbc:postgresql://localhost:5432/investment_portfolio
DB_USERNAME=postgres
DB_PASSWORD=password
```

### API Server
```bash
AUTH_SERVICE_HOST=localhost
AUTH_SERVICE_PORT=50050
```

---

## Testing the Integration

### 1. **Test Login**
```bash
curl -X POST http://localhost:8080/v1/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

### 2. **Test Protected Endpoint**
```bash
TOKEN="<token-from-login-response>"

curl -X GET http://localhost:8080/v1/api/portfolio/123 \
  -H "Authorization: Bearer $TOKEN"
```

### 3. **Test Invalid Token**
```bash
curl -X GET http://localhost:8080/v1/api/portfolio/123 \
  -H "Authorization: Bearer invalid-token"
  
# Should return: 401 Unauthorized
```

### 4. **Test Missing Auth Header**
```bash
curl -X GET http://localhost:8080/v1/api/portfolio/123

# Should return: 401 Unauthorized "Missing Authorization header"
```

---

## Ports Summary

| Service | HTTP | gRPC |
|---------|------|------|
| API Server | 8080 | - |
| Auth Service | 8082 | 50050 |
| Market Data Service | 8083 | 50052 |
| Portfolio Service | 8080 | 50053 |
| Transaction Service | 8081 | 50054 |

---

## Security Notes

1. **Password Encoding:** Uses BCrypt (via PasswordEncoder)
2. **JWT Secret:** Must be kept secure (use environment variables)
3. **Token Expiration:** Default 1 hour, configurable
4. **HTTPS:** Should be used in production
5. **Token Storage:** Client stores token in localStorage/sessionStorage
6. **CORS:** Configured in api-server (GlobalCorsFilter)

---

## Next Steps

1. **Start Auth Service:**
   ```bash
   cd auth-service
   mvn clean package
   java -jar target/auth-service-0.1.jar
   ```

2. **Start API Server:**
   ```bash
   cd api-server
   mvn clean package
   java -jar target/api-server-0.1.jar
   ```

3. **Test the flow:**
   - Login to get token
   - Use token for protected endpoints
   - Test token refresh

---

## Troubleshooting

### 401 Unauthorized on login
- Check auth-service is running on port 8082
- Verify database connection
- Check user exists in database

### Auth filter not validating tokens
- Check api-server can reach auth-service on gRPC (50050)
- Verify JWT_SECRET matches between services
- Check token is in "Bearer <token>" format

### gRPC connection refused
- Verify auth-service gRPC server running on 50050
- Check firewall/network connectivity
- Review auth-service logs for startup errors

---

## Files Created/Modified

### New Files (API Server)
- `src/main/java/com/sood/auth/dto/LoginRequest.java`
- `src/main/java/com/sood/auth/dto/LoginResponse.java`
- `src/main/java/com/sood/auth/dto/RegisterRequest.java`
- `src/main/java/com/sood/auth/dto/RegisterResponse.java`
- `src/main/java/com/sood/auth/dto/RefreshTokenRequest.java`
- `src/main/java/com/sood/auth/service/ApiAuthService.java`
- `src/main/java/com/sood/auth/controller/AuthRestController.java`

### New Files (Auth Service)
- `src/main/java/com/sood/auth/api/AuthRestController.java`
- `src/main/java/com/sood/auth/dto/RefreshTokenRequest.java`

### Modified Files
- `api-server/src/main/resources/application.yml` - Added auth gRPC channel
- `api-server/src/main/resources/application-local.yml` - Added auth gRPC channel
- `auth-service/src/main/resources/application.yml` - Fixed config + DB settings

---

## Status: ✅ COMPLETE

All integration components implemented and configured.
Ready for testing and deployment.
