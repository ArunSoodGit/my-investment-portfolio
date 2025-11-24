package com.sood.auth.application;

import com.sood.auth.application.command.LoginCommand;
import com.sood.auth.application.command.RegisterCommand;
import com.sood.auth.application.command.ValidateTokenCommand;
import com.sood.auth.application.result.LoginResult;
import com.sood.auth.application.result.RegisterResult;
import com.sood.auth.application.result.TokenValidationResult;
import com.sood.auth.domain.model.User;
import com.sood.auth.domain.port.TokenServicePort;
import com.sood.auth.domain.port.UserRepositoryPort;
import com.sood.auth.domain.service.PasswordPolicy;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class AuthApplicationService {

    private final UserRepositoryPort userRepository;
    private final PasswordPolicy passwordPolicy;
    private final TokenServicePort tokenService;

    public AuthApplicationService(final UserRepositoryPort userRepository, final PasswordPolicy passwordPolicy,
            final TokenServicePort tokenService) {
        this.userRepository = userRepository;
        this.passwordPolicy = passwordPolicy;
        this.tokenService = tokenService;
    }

    public LoginResult login(final LoginCommand command) {
        final Optional<User> userOpt = userRepository.findByUsername(command.username());
        if (userOpt.isEmpty()) return new LoginResult(false, "Invalid credentials", null);

        final User user = userOpt.get();
        if (!user.checkPassword(command.password(), passwordPolicy)) {
            return new LoginResult(false, "Invalid credentials", null);
        }

        user.login();
        userRepository.update(user);

        final String token = tokenService.generateToken(user.getId(), user.getUsername(), List.of(user.getRole()));
        return new LoginResult(true, "Login successful", token);
    }

    public RegisterResult register(final RegisterCommand command) {
        final String password = command.password();
        passwordPolicy.validate(password);

        final String username = command.username();
        if (userRepository.existsByUsername(username)) {
            return new RegisterResult(false, "Username exists", null);

        }

        final String hashedPassword = passwordPolicy.encodePassword(password);

        final User newUser = User.register(username, command.email(), hashedPassword, "ROLE_USER");
        final User saved = userRepository.save(newUser);

        return new RegisterResult(true, "Registration successful", saved.getId());
    }

    public TokenValidationResult validateToken(final ValidateTokenCommand command) {
        final boolean valid = tokenService.validateToken(command.token());
        if (valid) {
            return new TokenValidationResult(true, null);
        }
        return new TokenValidationResult(false, "Invalid or expired token");

    }
}