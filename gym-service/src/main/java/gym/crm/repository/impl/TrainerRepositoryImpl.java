package gym.crm.repository.impl;

import gym.crm.model.Trainer;
import gym.crm.model.Training;
import gym.crm.repository.TrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer.getId() == null) {
            entityManager.persist(trainer);
            return trainer;
        } else {
            return entityManager.merge(trainer);
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try {
            String jpql = "SELECT t FROM Trainer t WHERE t.user.username = :username";
            return entityManager.createQuery(jpql, Trainer.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Trainer> findAllByUserUsernameIn(List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return List.of();
        }
        String jpql = "SELECT t FROM Trainer t WHERE t.user.username IN :usernames";
        return entityManager.createQuery(jpql, Trainer.class)
                .setParameter("usernames", usernames)
                .getResultList();
    }

    @Override
    public List<Trainer> findAllActive() {
        String jpql = "SELECT t FROM Trainer t WHERE t.user.isActive = true";
        return entityManager.createQuery(jpql, Trainer.class).getResultList();
    }

    @Override
    public List<Training> getTrainingsByCriteria(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        StringBuilder jpql = new StringBuilder("SELECT tr FROM Training tr WHERE tr.trainer.user.username = :username");

        if (fromDate != null) jpql.append(" AND tr.trainingDate >= :fromDate");
        if (toDate != null) jpql.append(" AND tr.trainingDate <= :toDate");
        if (traineeName != null && !traineeName.isEmpty()) jpql.append(" AND tr.trainee.user.firstName = :traineeName");

        TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);

        query.setParameter("username", username);
        if (fromDate != null) query.setParameter("fromDate", fromDate);
        if (toDate != null) query.setParameter("toDate", toDate);
        if (traineeName != null && !traineeName.isEmpty()) query.setParameter("traineeName", traineeName);

        return query.getResultList();
    }

    @Override
    public boolean existsByUsername(String username) {
        String jpql = "SELECT COUNT(t) FROM Trainer t WHERE t.user.username = :username";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<Trainer> getUnassignedActiveTrainers(String traineeUsername) {
        String jpql = "SELECT t FROM Trainer t WHERE t.user.isActive = true AND t NOT IN " +
                "(SELECT tr FROM Trainee te JOIN te.trainers tr WHERE te.user.username = :username)";

        TypedQuery<Trainer> query = entityManager.createQuery(jpql, Trainer.class);
        query.setParameter("username", traineeUsername);

        return query.getResultList();
    }
}