package gym.bdd;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "gym.bdd")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "not @disabled")
public class RunCucumberTest {
    @MockitoBean
    private JmsTemplate jmsTemplate;
}