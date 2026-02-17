package gym.crm.service;

import gym.crm.model.User;
import gym.crm.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Authenticate: Success")
    void authenticate_Success() {
        User user = new User();
        user.setUsername("User");
        user.setPassword("pass123");

        when(userRepository.findByUsername("User")).thenReturn(Optional.of(user));

        boolean result = authService.authenticate("User", "pass123");
        assertTrue(result);
    }

    @Test
    @DisplayName("Authenticate: Fail (Wrong Password)")
    void authenticate_WrongPassword() {
        User user = new User();
        user.setUsername("User");
        user.setPassword("pass123");

        when(userRepository.findByUsername("User")).thenReturn(Optional.of(user));

        boolean result = authService.authenticate("User", "wrong");
        assertFalse(result);
    }

    @Test
    @DisplayName("Authenticate: Fail (User Not Found)")
    void authenticate_UserNotFound() {
        when(userRepository.findByUsername("Ghost")).thenReturn(Optional.empty());
        boolean result = authService.authenticate("Ghost", "pass");
        assertFalse(result);
    }

    @Test
    @DisplayName("Change Password: Success")
    void changePassword_Success() {
        User user = new User();
        user.setPassword("old");
        when(userRepository.findByUsername("User")).thenReturn(Optional.of(user));

        boolean result = authService.changePassword("User", "old", "new");

        assertTrue(result);
        assertEquals("new", user.getPassword());
        verify(userRepository).save(user);
    }
}