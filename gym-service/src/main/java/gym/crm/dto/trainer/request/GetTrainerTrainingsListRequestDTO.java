package gym.crm.dto.trainer.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTrainerTrainingsListRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private String traineeName;
}
