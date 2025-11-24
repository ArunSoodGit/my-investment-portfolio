package com.sood.auth.application.port;

import com.sood.auth.application.command.LoginCommand;
import com.sood.auth.application.command.ValidateTokenCommand;
import com.sood.auth.application.result.LoginResult;
import com.sood.auth.application.command.RegisterCommand;
import com.sood.auth.application.result.RegisterResult;
import com.sood.auth.application.result.TokenValidationResult;

public interface AuthServicePort {

    LoginResult login(LoginCommand command);

    RegisterResult register(RegisterCommand command);

    TokenValidationResult validateToken(ValidateTokenCommand token);
}