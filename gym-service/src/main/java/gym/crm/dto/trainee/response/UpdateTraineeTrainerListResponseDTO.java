package gym.crm.dto.trainee.response;

import gym.crm.dto.trainer.TrainerShortDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateTraineeTrainerListResponseDTO {
    private List<TrainerShortDTO> trainers;
}
