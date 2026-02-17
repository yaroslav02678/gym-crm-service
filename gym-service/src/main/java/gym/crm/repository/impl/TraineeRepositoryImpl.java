package gym.crm.repository.impl;

import gym.crm.model.Trainee;
import gym.crm.model.Training;
import gym.crm.repository.TraineeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee save(Trainee trainee) {
        if (trainee.getId() == null) {
            entityManager.persist(trainee);
            return trainee;
        } else {
            return entityManager.merge(trainee);
        }
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try {
            String jpql = "SELECT t FROM Trainee t WHERE t.user.username = :username";
            TypedQuery<Trainee> query = entityManager.createQuery(jpql, Trainee.class);
            query.setParameter("username", username);
            return query.getResultStream().findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        String jpql = "SELECT COUNT(t) FROM Trainee t WHERE t.user.username = :username";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public void deleteByUsername(String username) {
        Optional<Trainee> traineeOptional = findByUsername(username);

        traineeOptional.ifPresent(trainee -> {
            entityManager.remove(trainee);
            System.out.println("Trainee " + username + " deleted.");
        });
    }

    @Override
    public List<Training> getByCriteria(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        StringBuilder jpql = new StringBuilder("SELECT tr FROM Training tr WHERE tr.trainee.user.username = :username");

        if (fromDate != null) jpql.append(" AND tr.trainingDate >= :fromDate");
        if (toDate != null) jpql.append(" AND tr.trainingDate <= :toDate");
        if (trainingTypeName != null && !trainingTypeName.isEmpty()) jpql.append(" AND tr.trainingType.trainingTypeName = :trainingTypeName");
        if (trainerName != null && !trainerName.isEmpty()) jpql.append(" AND tr.trainer.user.firstName = :trainerName");

        TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);

        query.setParameter("username", username);
        if (fromDate != null) query.setParameter("fromDate", fromDate);
        if (toDate != null) query.setParameter("toDate", toDate);
        if (trainingTypeName != null && !trainingTypeName.isEmpty()) query.setParameter("trainingTypeName", trainingTypeName);
        if (trainerName != null && !trainerName.isEmpty()) query.setParameter("trainerName", trainerName);

        return query.getResultList();
    }
}
