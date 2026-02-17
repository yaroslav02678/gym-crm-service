package gym.crm.repository;

import gym.crm.model.Trainer;
import gym.crm.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Trainer save(Trainer trainer);
    Optional<Trainer> findByUsername(String username);
    List<Trainer> findAllByUserUsernameIn(List<String> usernames);
    List<Trainer> findAllActive();
    List<Training> getTrainingsByCriteria(String username, LocalDate fromDate, LocalDate toDate, String traineeName);
    boolean existsByUsername(String username);
    List<Trainer> getUnassignedActiveTrainers(String traineeUsername);
}