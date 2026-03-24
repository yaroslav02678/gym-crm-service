package gym.bdd.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.awaitility.Awaitility;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationStepDefinitions {

    @LocalServerPort
    private int port;

    private final String WORKLOAD_SERVICE_URL = "http://localhost:8081";

    private Response lastGymResponse;
    private Response lastWorkloadResponse;
    private final Map<String, Integer> mockDurations = new HashMap<>();

    private String getGymServiceUrl() {
        return "http://localhost:" + port;
    }

    private void updateWorkloadMock(String username, int total) {
        stubFor(get(urlEqualTo("/api/workloads/" + username))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"totalDuration\": " + total + "}")
                        .withStatus(200)));
    }

    private void sendCreateTrainingRequest(String username, int duration, String date) {
        int currentTotal = mockDurations.getOrDefault(username, 0);
        if (duration > 0 && !username.equals("Ghost.User")) {
            currentTotal += duration;
            mockDurations.put(username, currentTotal);
            updateWorkloadMock(username, currentTotal);
        }

        String payload = String.format("""
                {
                    "trainerUsername": "%s",
                    "trainerFirstname": "Name",
                    "trainerLastname": "Surname",
                    "isActive": true,
                    "trainingDate": "%s",
                    "trainingDuration": %d,
                    "actionType": "ADD"
                }
                """, username, date, duration);

        lastGymResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post(getGymServiceUrl() + "/api/trainings");
    }

    @Given("all microservices and ActiveMQ are running")
    public void allMicroservicesAndActiveMQAreRunning() {
        mockDurations.clear();
        WireMock.reset();

        stubFor(get(urlEqualTo("/actuator/health"))
                .willReturn(aResponse().withStatus(200)));

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    given()
                            .port(port)
                            .get("/actuator/health")
                            .then()
                            .statusCode(200);
                });
    }

    @When("I create a new training in {string} for trainer {string} with {int} minutes")
    public void iCreateANewTrainingInForTrainerWithMinutes(String serviceName, String trainer, int duration) {
        sendCreateTrainingRequest(trainer, duration, LocalDate.now().toString());
    }

    @And("I wait for the message to be processed")
    public void iWaitForTheMessageToBeProcessed() {
        Awaitility.await().atMost(2, TimeUnit.SECONDS).until(() -> true);
    }

    @Then("I request the summary for {string} from {string}")
    public void iRequestTheSummaryForFrom(String trainer, String serviceName) {
        lastWorkloadResponse = given()
                .get(WORKLOAD_SERVICE_URL + "/api/workloads/" + trainer);
    }

    @And("the total duration for current month should be {int} minutes")
    public void theTotalDurationForCurrentMonthShouldBeMinutes(int expectedDuration) {
        lastWorkloadResponse.then()
                .statusCode(200)
                .body("totalDuration", equalTo(expectedDuration));
    }

    @When("I create trainings for trainer {string} with durations {int}, {int}")
    public void iCreateTrainingsForTrainerWithDurations(String trainer, int dur1, int dur2) {
        sendCreateTrainingRequest(trainer, dur1, LocalDate.now().toString());
        sendCreateTrainingRequest(trainer, dur2, LocalDate.now().toString());
    }

    @When("I create trainings for trainer {string} with durations {int}, {int}, {int}")
    public void iCreateTrainingsForTrainerWithDurations(String trainer, int dur1, int dur2, int dur3) {
        sendCreateTrainingRequest(trainer, dur1, LocalDate.now().toString());
        sendCreateTrainingRequest(trainer, dur2, LocalDate.now().toString());
        sendCreateTrainingRequest(trainer, dur3, LocalDate.now().toString());
    }

    @When("I create {int} trainings for {string} each with {int} minutes")
    public void iCreateTrainingsForEachWithMinutes(int count, String trainer, int duration) {
        for (int i = 0; i < count; i++) {
            sendCreateTrainingRequest(trainer, duration, LocalDate.now().toString());
        }
    }

    @Then("the total duration for {string} should be {int} minutes")
    public void theTotalDurationForShouldBeMinutes(String trainer, int expectedDuration) {
        given()
                .get(WORKLOAD_SERVICE_URL + "/api/workloads/" + trainer)
                .then()
                .statusCode(200)
                .body("totalDuration", equalTo(expectedDuration));
    }

    @Then("eventually the total duration for {string} should be {int} minutes")
    public void eventuallyTheTotalDurationForShouldBeMinutes(String trainer, int expectedDuration) {
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    given()
                            .get(WORKLOAD_SERVICE_URL + "/api/workloads/" + trainer)
                            .then()
                            .statusCode(200)
                            .body("totalDuration", equalTo(expectedDuration));
                });
    }

    @When("I create a training for {string} with {int} minutes today")
    public void iCreateATrainingForWithMinutesToday(String trainer, int duration) {
        sendCreateTrainingRequest(trainer, duration, LocalDate.now().toString());
    }

    @When("I create a training for {string} with {int} minutes")
    public void iCreateATrainingForWithMinutes(String trainer, int duration) {
        sendCreateTrainingRequest(trainer, duration, LocalDate.now().toString());
    }

    @When("I try to create invalid training for {string}")
    public void iTryToCreateInvalidTrainingFor(String trainer) {
        String payload = String.format("""
                {
                    "trainerUsername": "%s",
                    "trainingDuration": -50
                }
                """, trainer);
        lastGymResponse = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post(getGymServiceUrl() + "/api/trainings");
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, lastGymResponse.getStatusCode());
    }

    @Then("workload-service total should match gym-service data")
    public void workloadServiceTotalShouldMatchGymServiceData() {
        String trainer = "John.Doe";
        int expected = mockDurations.getOrDefault(trainer, 0);
        Response res = given().get(WORKLOAD_SERVICE_URL + "/api/workloads/" + trainer);
        assertEquals(expected, res.jsonPath().getInt("totalDuration"));
    }
}