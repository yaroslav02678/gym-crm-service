package gym.crm.service;

import gym.crm.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CredentialService credentialService;

    @Test
    @DisplayName("Should generate simple username if not exists")
    void generateUsername_Simple() {
        when(userRepository.existsByUsername("John.Doe")).thenReturn(false);
        String result = credentialService.generateUsername("John", "Doe");
        assertEquals("John.Doe", result);
    }

    @Test
    @DisplayName("Should generate username with suffix if base exists")
    void generateUsername_WithSuffix() {
        when(userRepository.existsByUsername("John.Doe")).thenReturn(true);
        when(userRepository.existsByUsername("John.Doe1")).thenReturn(true);
        when(userRepository.existsByUsername("John.Doe2")).thenReturn(false);

        String result = credentialService.generateUsername("John", "Doe");

        assertEquals("John.Doe2", result);
    }

    @Test
    @DisplayName("Should throw exception if names are null")
    void generateUsername_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> credentialService.generateUsername(null, "Doe"));
    }

    @Test
    @DisplayName("Should generate password of length 10")
    void generateRandomPassword() {
        String pwd = credentialService.generateRandomPassword();
        assertNotNull(pwd);
        assertEquals(10, pwd.length());
    }
}