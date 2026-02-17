package gym.crm.dto.trainer.request;

import gym.crm.model.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerRegistrationRequestDTO {
    @NotBlank(message = "First name is required")
    @Size(min = 5, max = 30, message = "First name must be between 5 and 30 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 5, max = 30, message = "Last name must be between 5 and 30 characters")
    private String lastName;

    @NotNull(message = "specialization is required")
    private Long specializationId;
}
