package gym.trainerworkloadservice.repository;

import gym.trainerworkloadservice.model.TrainerSummary;
import gym.trainerworkloadservice.model.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerSummaryRepository extends MongoRepository<TrainerSummary, String> {
    Optional<TrainerSummary> findByUsername(String username);
}