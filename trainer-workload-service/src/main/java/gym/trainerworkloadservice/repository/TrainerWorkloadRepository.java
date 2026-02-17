package gym.trainerworkloadservice.repository;

import gym.trainerworkloadservice.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    Optional<TrainerWorkload> findByUsernameAndYearAndMonth(String username, int year, int month);

    List<TrainerWorkload> findAllByUsername(String username);
}