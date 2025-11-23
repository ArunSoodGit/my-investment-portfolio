package com.sood.auth.service;

import com.example.market.grpc.LoginRequest;
import com.example.market.grpc.LoginResponse;
import com.example.market.grpc.RegisterRequest;
import com.example.market.grpc.RegisterResponse;
import com.example.market.grpc.TokenValidationResponse;
import com.sood.auth.infrastructure.entity.UserEntity;
import com.sood.auth.infrastructure.repository.UserRepository;
import com.sood.auth.jwt.JwtClaimsExtractor;
import com.sood.auth.jwt.JwtTokenGenerator;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class AuthService {

    private final JwtTokenGenerator tokenGenerator;
    private final UserRepository userRepository;
    private final UserAuthValidator userAuthValidator;
    private final UserFactory userFactory;
    private final TokenValidator tokenValidator;
    private final JwtClaimsExtractor claimsExtractor;
    private final AuthResponseBuilder responseBuilder;
    private final long jwtExpiration;

    public AuthService(final JwtTokenGenerator tokenGenerator, final UserRepository userRepository,
            final UserAuthValidator userAuthValidator, final UserFactory userFactory,
            final TokenValidator tokenValidator, final JwtClaimsExtractor claimsExtractor,
            final AuthResponseBuilder responseBuilder, @Value("${jwt.expiration}") final long jwtExpiration) {
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
        this.userAuthValidator = userAuthValidator;
        this.userFactory = userFactory;
        this.tokenValidator = tokenValidator;
        this.claimsExtractor = claimsExtractor;
        this.responseBuilder = responseBuilder;
        this.jwtExpiration = jwtExpiration;
    }

    public LoginResponse login(final LoginRequest request) {
        final Optional<UserEntity> validatedUser = userAuthValidator.validateLoginCredentials(
                request.getUsername(),
                request.getPassword()
        );

        if (validatedUser.isEmpty()) {
            return responseBuilder.buildFailedLoginResponse("Invalid credentials");
        }

        final UserEntity user = validatedUser.get();
        final String token = tokenGenerator.generateToken(user.getId(), user.getUsername(), List.of(user.getRole()));

        final UserEntity updatedUser = userFactory.updateLastLogin(user);
        userRepository.update(updatedUser);

        log.info("Successful login for user: {}", user.getUsername());
        return responseBuilder.buildSuccessLoginResponse(token, jwtExpiration);
    }

    public RegisterResponse register(final RegisterRequest request) {
        if (userAuthValidator.usernameExists(request.getUsername())) {
            log.warn("Registration attempt with existing username: {}", request.getUsername());
            return responseBuilder.buildFailedRegisterResponse("Username already exists");
        }

        if (userAuthValidator.emailExists(request.getEmail())) {
            log.warn("Registration attempt with existing email: {}", request.getEmail());
            return responseBuilder.buildFailedRegisterResponse("Email already exists");
        }

        final UserEntity newUser = userFactory.createNewUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        final UserEntity savedUser = userRepository.save(newUser);

        log.info("New user registered: {}", savedUser.getUsername());
        return responseBuilder.buildSuccessRegisterResponse(savedUser.getId());
    }

    public TokenValidationResponse refreshTokenIfValid(final String token) {
        final TokenValidationResponse validation = validateToken(token);

        if (validation.getValid()) {
            log.info("Token validation successful for user: {}", validation.getUserId());
        }

        return validation;
    }

    public TokenValidationResponse validateToken(final String token) {
        final Optional<String> userId = tokenValidator.validateAndExtractUserId(token);

        if (userId.isEmpty()) {
            return responseBuilder.buildInvalidTokenResponse("Invalid or expired token");
        }

        final String username = claimsExtractor.extractUsername(token);
        final List<String> roles = claimsExtractor.extractRoles(token);
        final String tokenType = claimsExtractor.extractTokenType(token);

        return responseBuilder.buildValidTokenResponse(userId.get(), username, roles, tokenType);
    }
}
