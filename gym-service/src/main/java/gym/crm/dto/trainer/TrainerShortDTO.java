package gym.crm.dto.trainer;

import gym.crm.model.TrainingType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerShortDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
}
