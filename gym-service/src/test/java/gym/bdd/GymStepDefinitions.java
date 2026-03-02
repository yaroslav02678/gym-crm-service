package gym.bdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import gym.crm.dto.training.request.AddTrainingRequestDTO;
import gym.crm.repository.TrainingRepository;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@CucumberContextConfiguration
@SpringBootTest
public class GymStepDefinitions {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TrainingRepository trainingRepository;

    @MockitoBean
    private JmsTemplate jmsTemplate;

    @Given("the user is authenticated")
    public void authenticate() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("Alice.Smith", null, null)
        );
    }

    @When("I send a POST request to {string} with name {string} and duration {int}")
    public void sendPostRequest(String url, String name, Integer duration) throws Exception {
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("Alice.Smith");
        request.setTrainerUsername("John.Doe");
        request.setTrainingName(name);
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration((long) duration);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Then("the training should be saved in the database")
    public void verifyDatabase() {
        assertTrue(trainingRepository.count() > 0, "Training was not saved in H2");
    }

    @And("a message should be sent to the {string}")
    public void verifyJmsMessage(String queueName) {
        verify(jmsTemplate).convertAndSend(eq(queueName), any(Object.class));
    }
}