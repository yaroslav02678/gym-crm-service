package gym.trainerworkloadservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trainer_workload")
public class TrainerWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;

    @Column(name = "training_year")
    private int year;

    @Column(name = "training_month")
    private int month;

    private long totalDuration;
}