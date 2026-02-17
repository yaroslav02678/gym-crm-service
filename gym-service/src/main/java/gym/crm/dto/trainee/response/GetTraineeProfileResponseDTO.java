package gym.crm.dto.trainee.response;

import gym.crm.dto.trainer.TrainerShortDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTraineeProfileResponseDTO {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private boolean isActive;
    private List<TrainerShortDTO> trainersList;
}
