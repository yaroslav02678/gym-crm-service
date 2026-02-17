package gym.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public User(String firstName, String lastName, boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    @Column(name = "login_attempts")
    private int loginAttempts = 0;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    public boolean isAccountNonLocked() {
        if (lockTime == null) {
            return true;
        }
        if (lockTime.plusMinutes(5).isBefore(LocalDateTime.now())) {
            this.lockTime = null;
            this.loginAttempts = 0;
            return true;
        }
        return false;
    }

    public boolean isActive() {
        return isActive;
    }
}
