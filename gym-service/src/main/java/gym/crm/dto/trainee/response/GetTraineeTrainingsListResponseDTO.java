package gym.crm.dto.trainee.response;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTraineeTrainingsListResponseDTO {
    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private Number trainingDuration;
    private String trainerName;
}
