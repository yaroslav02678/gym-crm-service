package gym.crm.controller;

import gym.crm.dto.login.ChangeLoginRequestDTO;
import gym.crm.dto.login.LoginRequestDTO;
import gym.crm.security.JwtService;
import gym.crm.service.AuthService;
import gym.crm.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<Void> login(@Valid @ModelAttribute LoginRequestDTO request,
                                      HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            loginAttemptService.loginSucceeded(request.getUsername());

            String token = jwtService.generateToken(request.getUsername());
            response.addHeader("Authorization", "Bearer " + token);
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(request.getUsername());
            throw e;
        } catch (LockedException e) {
            throw new RuntimeException("Account is locked due to too many failed attempts. Try again in 5 minutes.");
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangeLoginRequestDTO request) {
        boolean isChanged = authService.changePassword(
                request.getUsername(),
                request.getOldPassword(),
                request.getNewPassword()
        );
        if (isChanged) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}