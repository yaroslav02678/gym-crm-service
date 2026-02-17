package gym.crm.repository.impl;

import gym.crm.model.Trainee;
import gym.crm.model.TrainingType;
import gym.crm.repository.TrainingTypeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<TrainingType> findById(Long id) {
        TrainingType type = entityManager.find(TrainingType.class, id);
        return Optional.ofNullable(type);
    }

    @Override
    public List<TrainingType> findAll() {
        return entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class)
                .getResultList();
    }

    @Override
    public TrainingType save(TrainingType trainingType) {
        if (trainingType.getId() == null) {
            entityManager.persist(trainingType);
            return trainingType;
        } else {
            return entityManager.merge(trainingType);
        }
    }
}