package gym.crm.dto.trainer.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateTrainerProfileRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private Long specializationId;

    @NotBlank(message = "Status is required")
    private boolean isActive;

    public boolean getIsActive() {
        return isActive;
    }
}
