package gym.crm.repository;

import gym.crm.model.Trainee;
import gym.crm.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository {
    Trainee save(Trainee trainee);
    Optional<Trainee> findByUsername(String username);
    void deleteByUsername(String username);
    boolean existsByUsername(String username);
    List<Training> getByCriteria(String username, LocalDate from, LocalDate to, String trainerName, String trainingType);
}