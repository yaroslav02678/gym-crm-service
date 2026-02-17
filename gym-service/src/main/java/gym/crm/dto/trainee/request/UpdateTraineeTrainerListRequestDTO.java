package gym.crm.dto.trainee.request;

import gym.crm.dto.trainer.TrainerUsernameDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateTraineeTrainerListRequestDTO {
    private String traineeUsername;

    @NotBlank(message = "Trainer username is required")
    private List<TrainerUsernameDTO> trainerUsernames;
}
