package gym.crm.dto.trainer.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTrainerProfileRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;
}
