package gym.crm.service;

import gym.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialService {

    private final UserRepository userRepository;

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    @Transactional(readOnly = true)
    public String generateUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and Last name cannot be null");
        }
        String base = firstName + "." + lastName;
        if (!userRepository.existsByUsername(base)) {
            log.debug("Generated base username: {}", base);
            return base;
        }

        int serial = 1;
        String finalUsername;
        do {
            finalUsername = base + serial;
            serial++;
        } while (userRepository.existsByUsername(finalUsername));

        log.debug("Generated username with suffix: {}", finalUsername);
        return finalUsername;
    }

    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(randomIndex));
        }

        return sb.toString();
    }
}