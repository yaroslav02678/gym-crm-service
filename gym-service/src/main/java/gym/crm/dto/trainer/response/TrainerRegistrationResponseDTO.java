package gym.crm.dto.trainer.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerRegistrationResponseDTO {
    private String username;
    private String password;
}
