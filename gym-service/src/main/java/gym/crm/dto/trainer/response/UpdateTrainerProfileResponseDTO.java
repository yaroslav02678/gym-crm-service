package gym.crm.dto.trainer.response;

import gym.crm.dto.trainee.TraineeShortDTO;
import gym.crm.dto.trainer.TrainerShortDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateTrainerProfileResponseDTO {
    private String username;
    private String firstName;
    private String lastName;
    private Long specialization;
    private boolean isActive;
    private List<TraineeShortDTO> traineesList;
}
