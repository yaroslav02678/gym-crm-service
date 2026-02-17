package gym.crm.repository;

import gym.crm.model.Trainee;
import gym.crm.model.TrainingType;
import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {
    Optional<TrainingType> findById(Long id);
    List<TrainingType> findAll();
    TrainingType save(TrainingType trainingType);

}