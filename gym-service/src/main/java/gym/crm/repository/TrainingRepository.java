package gym.crm.repository;

import gym.crm.model.Training;

import java.util.Optional;

public interface TrainingRepository {
    Training save(Training training);
}
