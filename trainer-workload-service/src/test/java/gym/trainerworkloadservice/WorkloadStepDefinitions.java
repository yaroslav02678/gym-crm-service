package gym.trainerworkloadservice;

import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import gym.trainerworkloadservice.model.*;
import gym.trainerworkloadservice.repository.TrainerSummaryRepository;
import gym.trainerworkloadservice.service.WorkloadService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@CucumberContextConfiguration
@SpringBootTest
public class WorkloadStepDefinitions {

    @Autowired
    private WorkloadService workloadService;

    @Autowired
    private TrainerSummaryRepository repository;

    @Given("a trainer {string} exists in MongoDB with {int} minutes in {string} {int}")
    public void seedData(String username, Integer minutes, String monthName, Integer year) {
        repository.deleteAll();
        int monthValue = Month.valueOf(monthName.toUpperCase()).getValue();

        List<MonthSummary> months = new ArrayList<>();
        months.add(new MonthSummary(monthValue, (long) minutes));

        List<YearSummary> years = new ArrayList<>();
        years.add(new YearSummary(year, months));

        TrainerSummary summary = TrainerSummary.builder()
                .username(username)
                .firstName("Test")
                .lastName("Trainer")
                .status(true)
                .years(years)
                .build();

        repository.save(summary);
    }

    @When("the service processes an {string} request for {string} with {int} minutes for date {string}")
    public void processAnRequest(String action, String username, Integer duration, String date) {
        executeTransaction(action, username, duration, date);
    }

    @When("the service processes a {string} request for {string} with {int} minutes for date {string}")
    public void processARequest(String action, String username, Integer duration, String date) {
        executeTransaction(action, username, duration, date);
    }

    private void executeTransaction(String action, String username, Integer duration, String date) {
        TrainerWorkloadRequest request = TrainerWorkloadRequest.builder()
                .trainerUsername(username)
                .trainingDate(LocalDate.parse(date))
                .trainingDuration(duration)
                .actionType(TrainerWorkloadRequest.ActionType.valueOf(action.toUpperCase()))
                .transactionId("test-transaction-id")
                .build();

        workloadService.processTransaction(request);
    }

    @Then("the MongoDB summary for {string} should have {int} minutes in {string} {int}")
    public void verify(String username, Integer expectedMinutes, String monthName, Integer year) {
        int monthValue = Month.valueOf(monthName.toUpperCase()).getValue();
        TrainerSummary summary = workloadService.getTrainerSummary(username);

        assertNotNull(summary);

        long actualDuration = summary.getYears().stream()
                .filter(y -> y.getYear() == year)
                .flatMap(y -> y.getMonths().stream())
                .filter(m -> m.getMonthValue() == monthValue)
                .map(MonthSummary::getDuration)
                .findFirst()
                .orElse(-1L);

        assertEquals((long) expectedMinutes, actualDuration);
    }
}