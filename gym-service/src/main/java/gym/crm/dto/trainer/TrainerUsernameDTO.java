package gym.crm.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerUsernameDTO {
    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;
}
