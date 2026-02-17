package gym.crm.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricServiceTest {

    @Test
    void incrementLoginCounter() {
        SimpleMeterRegistry simpleRegistry = new SimpleMeterRegistry();
        MetricService metricService = new MetricService(simpleRegistry);

        metricService.incrementLoginCounter();

        double count = simpleRegistry
                .find("user.login.count")
                .counter()
                .count();

        assertEquals(1.0, count);
    }
}