package gym.crm.dto.trainer.response;

import gym.crm.dto.trainee.TraineeShortDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTrainerProfileResponseDTO {
    private String firstName;
    private String lastName;
    private Long specialization;
    private boolean isActive;
    private List<TraineeShortDTO> traineesList;
}
