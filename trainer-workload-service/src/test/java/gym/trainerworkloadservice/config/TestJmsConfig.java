package gym.trainerworkloadservice.config;

import jakarta.jms.ConnectionFactory;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

@TestConfiguration
public class TestJmsConfig {
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(Mockito.mock(ConnectionFactory.class));
        return factory;
    }
}