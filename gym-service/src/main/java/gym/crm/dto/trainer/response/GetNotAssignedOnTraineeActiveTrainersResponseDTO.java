package gym.crm.dto.trainer.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetNotAssignedOnTraineeActiveTrainersResponseDTO {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Long trainerSpecialization;
}
