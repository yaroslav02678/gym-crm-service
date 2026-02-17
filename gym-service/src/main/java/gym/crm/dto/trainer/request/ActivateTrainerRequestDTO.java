package gym.crm.dto.trainer.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ActivateTrainerRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Status is required")
    private boolean isActive;
}
