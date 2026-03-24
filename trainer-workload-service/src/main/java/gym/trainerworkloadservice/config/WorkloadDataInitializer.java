package gym.trainerworkloadservice.config;

import gym.trainerworkloadservice.model.MonthSummary;
import gym.trainerworkloadservice.model.TrainerSummary;
import gym.trainerworkloadservice.model.TrainerWorkload;
import gym.trainerworkloadservice.model.YearSummary;
import gym.trainerworkloadservice.repository.TrainerSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkloadDataInitializer implements CommandLineRunner {

    private final TrainerSummaryRepository repository;

    @Override
    public void run(String... args) {
        repository.deleteAll();

//        List<MonthSummary> months2025 = new ArrayList<>();
//        months2025.add(new MonthSummary(5, 120L));
//        months2025.add(new MonthSummary(6, 60L));
//
//        List<YearSummary> years = new ArrayList<>();
//        years.add(new YearSummary(2025, months2025));
//
//        TrainerSummary johnDoe = TrainerSummary.builder()
//                .username("John.Doe")
//                .firstName("John")
//                .lastName("Doe")
//                .status(true)
//                .years(years)
//                .build();
//
//        repository.save(johnDoe);
//
//        System.out.println(">> Workload Service: Initial NoSQL document loaded for John.Doe in MongoDB");
    }
}