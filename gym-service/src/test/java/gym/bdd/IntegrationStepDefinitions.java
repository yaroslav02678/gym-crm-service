package gym.bdd;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationStepDefinitions {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<Map> workloadResponse;
    private final String gymUrl = "http://localhost:8080";
    private final String workloadUrl = "http://localhost:8081";
    private String jwtToken;

    @Given("all microservices and ActiveMQ are running")
    public void checkSystem() {
        var health = restTemplate.getForEntity("http://localhost:8080/actuator/health", String.class);
        assertEquals(HttpStatus.OK, health.getStatusCode(), "Gym service is down!");

        Map<String, String> loginReq = Map.of("username", "Alice.Smith", "password", "password123");
        var authRes = restTemplate.postForEntity("http://localhost:8080/api/auth/login", loginReq, Map.class);
        this.jwtToken = authRes.getBody().get("token").toString();
    }

    @When("I create a new training in {string} for trainer {string} with {int} minutes")
    public void createTrainingIntegrated(String service, String trainer, int duration) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        Map<String, Object> body = Map.of(
                "traineeUsername", "Alice.Smith",
                "trainerUsername", trainer,
                "trainingName", "Integration Yoga",
                "trainingDate", "2026-05-10",
                "trainingDuration", duration
        );
        restTemplate.postForEntity(gymUrl + "/api/trainings", new HttpEntity<>(body, headers), Void.class);
    }

    @And("I wait for message to be processed")
    public void waitProcessing() throws InterruptedException {
        Thread.sleep(3000);
    }

    @Then("I request the summary for {string} from {string}")
    public void requestSummary(String trainer, String service) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        workloadResponse = restTemplate.exchange(
                workloadUrl + "/api/v1/workload/" + trainer,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );
    }

    @And("the total duration for current month should be {int} minutes")
    public void verifyResult(int expected){
        assertNotNull(workloadResponse.getBody());
        assertTrue(workloadResponse.getBody().toString().contains("duration=" + expected));
    }
}
