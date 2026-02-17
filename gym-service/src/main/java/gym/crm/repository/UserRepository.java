package gym.crm.repository;

import gym.crm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> getById(Long id);
    Optional<User> findByUsername(String username);
    void deleteById(Long id);
    boolean existsByUsername(String username);
    List<User> findAll();
}
