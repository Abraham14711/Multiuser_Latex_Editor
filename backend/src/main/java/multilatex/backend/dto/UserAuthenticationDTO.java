package multilatex.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthenticationDTO {

    @NotBlank(message = "password cannot be blank")
    @Size(min = 6, max = 25, message = "password must be from 6 to 25 symbols")
    private String password;

    @NotBlank(message = "email cannot be blank")
    @Size(min = 4, max = 25, message = "email size must be from 4 to 25 symbols")
    private String email;
}
