package gym.crm.service;

import gym.crm.model.User;
import gym.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public boolean authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String dbPassword = user.getPassword();

            if (dbPassword != null && dbPassword.trim().equals(password.trim())) {
                log.info("Login SUCCESS for: {}", username);
                return true;
            }
        }

        log.warn("Login FAILED for: {}", username);
        return false;
    }

    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                userRepository.save(user);
                log.info("Password changed successfully for user: {}", username);
                return true;
            }
        }
        log.warn("Password change failed for user: {}", username);
        return false;
    }
}