package multilatex.backend.service.auth;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.annotation.PostConstruct;
import multilatex.backend.entities.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class AccessTokenService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${auth.acessTokenValidaty}")
    private Duration accessTokenValidity;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secretKey);
    }

    public void verifyToken(String token) throws JWTVerificationException {
        JWT.require(algorithm).build().verify(token);
    }

    public String extractUsername(String token) {
        return JWT.decode(token).getSubject();
    }

    public String extractEmail(String token) {
        return JWT.decode(token).getClaim("email").asString();
    }

    public String generateAccessToken(Users user) {
        return JWT.create().withIssuer("jwtService")
                .withSubject(user.getUsername())
                .withClaim("email", user.getEmail())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now()
                .plusSeconds(accessTokenValidity.getSeconds()))
                .sign(algorithm);
        //issuer - кто создал токен,
        //withsubject - имя пользователя,
        //withIssuedAt - время создания токена,
        //withExpiresAt - время истечения срока действия токена,
        //sign - подписание токена с использованием алгоритма
    }
}
