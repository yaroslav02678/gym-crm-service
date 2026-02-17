package gym.crm.dto.training;


import gym.crm.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingTypeDTO {
    private String trainingType;
    private Long trainingTypeId;
}
