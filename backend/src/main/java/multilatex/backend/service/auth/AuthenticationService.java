package multilatex.backend.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import multilatex.backend.dto.*;
import multilatex.backend.entities.RefreshToken;
import multilatex.backend.entities.Roles;
import multilatex.backend.entities.Users;
import multilatex.backend.excpetions.RefreshTokenNotFoundException;
import multilatex.backend.excpetions.RefreshTokenTimeoutException;
import multilatex.backend.excpetions.UsernameExistsException;
import multilatex.backend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    public CredentialsDTO register(UserRegistrationDTO userRegistrationDTO) throws UsernameExistsException {
        try {
            //проверяем корректность у почты
            String email = userRegistrationDTO.getEmail();
            if (isEmail(email)) {
                if (userRepository.existsByEmail(email)) {
                    throw new UsernameExistsException("Email " + email + " already exists");
                }
            } else {
                throw new IllegalArgumentException("Email " + email + " is not valid");
            }

            //сохраняем пользвоателя
            Users user = Users.builder()
                    .username(userRegistrationDTO.getUsername())
                    .email(email)
                    .password(passwordEncoder.encode(userRegistrationDTO.getPassword()))
                    .role(Roles.USER)
                    .build();
            userRepository.save(user);

            log.info("User registered successfully");
            return generateCredentials(user);
        } catch (UsernameExistsException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public CredentialsDTO logInUser(UserAuthenticationDTO userAuthenticationDTO) {
        String email = userAuthenticationDTO.getEmail();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, userAuthenticationDTO.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Bad credentials");
        }
        
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        log.info("User {} logged in successfully.", userAuthenticationDTO.getEmail());
        return generateCredentials(user);
    }
    private boolean isEmail(String email) {
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,5}$");
    }

    public AccessTokenDTO getRefreshedToken(RefreshTokenDTO refreshTokenDTO) throws RefreshTokenNotFoundException, RefreshTokenTimeoutException {
        try {
            RefreshToken refreshToken = refreshTokenService.getRefreshToken(refreshTokenDTO.getToken());
            if (!refreshTokenService.isExpired(refreshToken)) {
                Users user = refreshToken.getUser();
                String accessToken = accessTokenService.generateAccessToken(user);
                log.info("Token updated successfully: {}\nNew token: {}", refreshTokenDTO.getToken(), accessToken);
                return new AccessTokenDTO(accessToken);
            }
            refreshTokenService.removeToken(refreshTokenDTO.getToken());
            throw new RefreshTokenTimeoutException("Refresh token timeout");
        } catch (RefreshTokenNotFoundException | RefreshTokenTimeoutException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private CredentialsDTO generateCredentials(Users user) {
        String accessToken = accessTokenService.generateAccessToken(user);
        UUID refreshToken = refreshTokenService.generateRefreshToken(user);
        return CredentialsDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(Roles.USER)
                .build();
    }
}
