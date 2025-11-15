package multilatex.backend.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import multilatex.backend.dto.*;
import multilatex.backend.excpetions.RefreshTokenNotFoundException;
import multilatex.backend.excpetions.RefreshTokenTimeoutException;
import multilatex.backend.excpetions.UsernameExistsException;
import multilatex.backend.service.auth.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<CredentialsDTO> register(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) throws UsernameExistsException {
        CredentialsDTO credentialsDTO = authenticationService.register(userRegistrationDTO);
        return ResponseEntity.ok(credentialsDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<CredentialsDTO> logInUser(@Validated @RequestBody UserAuthenticationDTO userAuthenticationDTO) throws AuthenticationException {
        CredentialsDTO credentialsDTO = authenticationService.logInUser(userAuthenticationDTO);
        return ResponseEntity.ok(credentialsDTO);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AccessTokenDTO> refreshToken(@Validated @RequestBody RefreshTokenDTO refreshTokenDTO) throws RefreshTokenNotFoundException, RefreshTokenTimeoutException {
        AccessTokenDTO newToken = authenticationService.getRefreshedToken(refreshTokenDTO);
        return ResponseEntity.ok(newToken);
    }


}
