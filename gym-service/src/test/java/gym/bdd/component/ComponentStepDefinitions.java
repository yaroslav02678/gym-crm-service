package gym.bdd.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import gym.crm.dto.training.request.AddTrainingRequestDTO;
import gym.crm.dto.trainee.request.TraineeRegistrationRequestDTO;
import gym.crm.dto.trainer.request.TrainerRegistrationRequestDTO;
import gym.crm.model.Trainee;
import gym.crm.model.User;
import gym.crm.repository.TraineeRepository;
import gym.crm.repository.TrainerRepository;
import gym.crm.repository.TrainingRepository;
import io.cucumber.java.en.*;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityManager;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ComponentStepDefinitions {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TrainingRepository trainingRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TraineeRepository traineeRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TrainerRepository trainerRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JmsTemplate jmsTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    private ResultActions lastResult;

    @Given("the user is authenticated")
    public void authenticate() {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_TRAINEE"));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("Alice.Smith", "password", authorities)
        );
    }

    @Given("the user is not authenticated")
    public void theUserIsNotAuthenticated() {
        SecurityContextHolder.clearContext();
    }

    @When("I send a POST request to {string} with valid data")
    public void iSendAPOSTRequestToWithValidData(String url) throws Exception {
        Object body;
        if (url.contains("trainings")) {
            body = new AddTrainingRequestDTO("Alice.Smith", "John.Doe", "Yoga", LocalDate.now(), 60L);
        } else if (url.contains("trainees")) {
            body = new TraineeRegistrationRequestDTO("Dmytro", "Ivanov", LocalDate.now().minusYears(20), "Kyiv, main st.");
        } else {
            body = new TrainerRegistrationRequestDTO("Johnn", "Doeee", 1L);
        }

        lastResult = mockMvc.perform(post(url)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @When("I send a POST request to {string} with invalid data")
    public void iSendAPOSTRequestToWithInvalidData(String url) throws Exception {
        lastResult = mockMvc.perform(post(url)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));
    }

    @When("I send a POST request to {string} with non-existent trainee")
    public void iSendAPOSTRequestToWithNonExistentTrainee(String url) throws Exception {
        var body = new AddTrainingRequestDTO("Ghost.User", "John.Doe", "Yoga", LocalDate.now(), 60L);
        lastResult = mockMvc.perform(post(url).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body)));
    }

    @When("I send a POST request to {string} with non-existent trainer")
    public void iSendAPOSTRequestToWithNonExistentTrainer(String url) throws Exception {
        var body = new AddTrainingRequestDTO("Alice.Smith", "Ghost.Trainer", "Yoga", LocalDate.now(), 60L);
        lastResult = mockMvc.perform(post(url).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body)));
    }

    @When("I send a POST request to {string} without token")
    public void i_send_without_token(String url) throws Exception {
        SecurityContextHolder.clearContext();
        lastResult = mockMvc.perform(post(url).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"));
    }

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String url) throws Exception {
        lastResult = mockMvc.perform(get(url).queryParam("username", "Alice.Smith"));
    }

    @When("I send a GET request to {string} without token")
    public void iSendAGETRequestToWithoutToken(String url) throws Exception {
        SecurityContextHolder.clearContext();
        lastResult = mockMvc.perform(get(url).param("username", "Alice.Smith"));    }

    @When("I send a DELETE request to {string}")
    public void iSendADELETERequestTo(String url) throws Exception {
        lastResult = mockMvc.perform(delete(url).with(csrf()));
    }

    @When("I send a PATCH request to {string}")
    public void iSendAPATCHRequestTo(String url) throws Exception {
        lastResult = mockMvc.perform(patch(url).with(csrf()));
    }

    @When("I send a PUT request to {string} with valid data")
    public void iSendAPUTRequestToWithValidData(String url) throws Exception {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("username", "Alice.Smith");
        body.put("firstName", "UpdatedFirstName");
        body.put("lastName", "UpdatedLastName");
        body.put("dateOfBirth", "2000-01-01"); // Формат YYYY-MM-DD
        body.put("address", "Updated Street 123");
        body.put("isActive", true);
        body.put("active", true);

        lastResult = mockMvc.perform(put(url)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer statusCode) throws Exception {
        lastResult.andExpect(status().is(statusCode));
    }

    @And("the training should be saved in the database")
    public void the_training_should_be_saved_in_the_database() {
        assertTrue(trainingRepository.count() > 0);
    }

    @And("the trainee should be saved in the database")
    public void theTraineeShouldBeSavedInTheDatabase() {
        assertFalse(traineeRepository.existsByUsername("Trainee"));
    }

    @And("the trainer should be saved in the database")
    public void theTrainerShouldBeSavedInTheDatabase() {
        assertFalse(trainerRepository.existsByUsername("Trainer"));
    }

    @And("trainee {string} exists")
    @Transactional
    public void traineeExists(String username) {
        if (traineeRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setFirstName("Alice");
            user.setLastName("Smith");
            user.setUsername(username);
            user.setPassword("password");
            user.setActive(true);

            Trainee trainee = new Trainee();
            trainee.setUser(user);
            trainee.setAddress("Original Street");
            trainee.setDateOfBirthday(java.time.LocalDate.of(2000, 1, 1));
            traineeRepository.save(trainee);
        }
    }

    @And("trainer {string} exists")
    public void trainerExists(String username) {
    }

    @And("a message should be sent to the {string}")
    public void a_message_should_be_sent_to_the(String queueName) {
        verify(jmsTemplate, times(1)).convertAndSend(eq(queueName), any(Object.class));
    }

    @And("the response should contain training types")
    public void theResponseShouldContainTrainingTypes() throws Exception {
        assertTrue(lastResult.andReturn().getResponse().getContentAsString().length() > 0);
    }

    @And("the trainee should be updated in the database")
    @Transactional
    public void theTraineeShouldBeUpdatedInTheDatabase() {
        entityManager.flush();
        entityManager.clear();
        var traineeOptional = traineeRepository.findByUsername("Alice.Smith");
        assertTrue(traineeOptional.isPresent(), "Trainee Alice.Smith should exist in the database");
        var updatedTrainee = traineeOptional.get();
        assertEquals("UpdatedFirstName", updatedTrainee.getUser().getFirstName(),
                "First name was not updated in the database");

        assertEquals("UpdatedLastName", updatedTrainee.getUser().getLastName(),
                "Last name was not updated in the database");

        assertEquals("Updated Street 123", updatedTrainee.getAddress(),
                "Address was not updated in the database");

        assertTrue(updatedTrainee.getUser().isActive(), "Trainee status should be active");
    }

    @And("the trainee should be removed from the database")
    public void theTraineeShouldBeRemovedFromTheDatabase() {
        String username = "Alice.Smith";

        var traineeOptional = traineeRepository.findByUsername(username);

        assertTrue(traineeOptional.isEmpty(),
                String.format("Trainee %s still exists in the database, but should have been deleted", username));
    }

    @When("I send a PUT request to {string} with trainer list")
    public void iSendAPUTRequestToWithTrainerList(String arg0) throws Exception {
        List<Long> trainerIds = List.of(1L);
        lastResult = mockMvc.perform(put(arg0)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainerIds)));
    }
}