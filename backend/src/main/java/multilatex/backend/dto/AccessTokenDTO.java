package multilatex.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessTokenDTO {
    @NotBlank(message = "Access token cannot be empty")
    private String token;
}
