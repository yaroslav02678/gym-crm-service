package gym.crm.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isServiceUp = checkExternalService();

        if (isServiceUp) {
            return Health.up().withDetail("ExternalService", "Available").build();
        } else {
            return Health.down().withDetail("ExternalService", "Unreachable").build();
        }
    }

    private boolean checkExternalService() {
        return new Random().nextBoolean();
    }
}