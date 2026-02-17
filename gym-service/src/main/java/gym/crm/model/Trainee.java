package gym.crm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "trainees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainee implements Serializable {
    @Id
    @Column(name = "trainee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_of_birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirthday;

    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Training> trainings = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "trainee_trainers",
              joinColumns = @JoinColumn(name = "trainee_id"),
              inverseJoinColumns = @JoinColumn(name = "trainer_id")
              )
    private Set<Trainer> trainers = new LinkedHashSet<>();

    public Trainee(LocalDate dateOfBirthday, String address, User user) {
        this.dateOfBirthday = dateOfBirthday;
        this.address = address;
        this.user = user;
    }

    public void addTrainer(Trainer trainer){
        this.trainers.add(trainer);
        trainer.getTrainees().add(this);
    }

    public void removeTrainer(Trainer trainer){
        this.trainers.remove(trainer);
        trainer.getTrainees().remove(this);
    }
}
