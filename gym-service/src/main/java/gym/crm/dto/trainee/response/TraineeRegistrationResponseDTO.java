package gym.crm.dto.trainee.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TraineeRegistrationResponseDTO {
    private String username;
    private String password;
}
