package gym.crm.controller;

import gym.crm.dto.login.ChangeLoginRequestDTO;
import gym.crm.dto.login.LoginRequestDTO;
import gym.crm.security.JwtService;
import gym.crm.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @MockitoBean private AuthenticationManager authenticationManager;
    @Mock private AuthService authService;
    @Mock private JwtService jwtService;
    @Mock private HttpServletResponse response;

    @InjectMocks private AuthController authController;

    @Test
    void login_Success() {
        LoginRequestDTO req = new LoginRequestDTO("User", "Pass");
        when(authService.authenticate("User", "Pass")).thenReturn(true);
        when(jwtService.generateToken("User")).thenReturn("token123");

        ResponseEntity<Void> res = authController.login(req, response);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        verify(response).addHeader("Authorization", "Bearer token123");
    }

    @Test
    void login_Fail() {
        LoginRequestDTO req = new LoginRequestDTO("User", "Wrong");
        when(authService.authenticate("User", "Wrong")).thenReturn(false);

        ResponseEntity<Void> res = authController.login(req, response);

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void changePassword_Success() {
        ChangeLoginRequestDTO req = new ChangeLoginRequestDTO("User", "Old", "New");

        when(authService.changePassword("User", "Old", "New")).thenReturn(true);

        ResponseEntity<Void> res = authController.changePassword(req);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void changePassword_Fail() {
        ChangeLoginRequestDTO req = new ChangeLoginRequestDTO("User", "Old", "New");

        when(authService.changePassword("User", "Old", "New")).thenReturn(false);

        ResponseEntity<Void> res = authController.changePassword(req);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }
}