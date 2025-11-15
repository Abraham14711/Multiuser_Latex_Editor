package multilatex.backend.dto;

import lombok.*;
import multilatex.backend.entities.Roles;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredentialsDTO { //отдаём сразу 2 штуки после логина
    private String accessToken;
    private UUID refreshToken;
    private Roles role;
}
