package gym.crm.repository.impl;

import gym.crm.model.Training;
import gym.crm.repository.TrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Training save(Training training) {
        if (training.getId() == null) {
            entityManager.persist(training);
            return training;
        } else {
            return entityManager.merge(training);
        }
    }

    @Override
    public long count() {
        return entityManager.createQuery("SELECT COUNT(t) FROM Training t", Long.class)
                .getSingleResult();
    }
}