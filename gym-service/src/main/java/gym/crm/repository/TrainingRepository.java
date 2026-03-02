package gym.crm.repository;

import gym.crm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingRepository {
    Training save(Training training);
    long count();
}
