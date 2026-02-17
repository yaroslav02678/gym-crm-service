package gym.crm.dto.trainee.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TraineeRegistrationRequestDTO {
    @NotBlank(message = "First name is required")
    @Size(min = 5, max = 30, message = "First name must be between 5 and 30 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 5, max = 30, message = "Last name must be between 5 and 30 characters")
    private String lastName;

    @Past(message = "Date of birth must be in past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(min = 5, max = 100, message = "addres must be between 5 and 100 characters")
    private String address;
}
