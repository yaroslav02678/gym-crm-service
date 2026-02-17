package gym.crm.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricService {

    private final Counter userLoginCounter;

    public MetricService(MeterRegistry registry) {
        this.userLoginCounter = Counter.builder("user.login.count")
                .description("Total number of user logins")
                .register(registry);
    }

    public void incrementLoginCounter() {
        userLoginCounter.increment();
    }
}