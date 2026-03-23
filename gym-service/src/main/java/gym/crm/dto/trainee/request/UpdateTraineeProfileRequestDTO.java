package gym.crm.dto.trainee.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateTraineeProfileRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private LocalDate dateOfBirth;

    @Size(min = 5, max = 30, message = "address must be between 5 and 30 characters")
    private String address;

    @NotNull(message = "Status is required")
    private boolean isActive;

    public boolean getIsActive() {
        return isActive;
    }
}
