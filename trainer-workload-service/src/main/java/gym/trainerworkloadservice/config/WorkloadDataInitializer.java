package gym.trainerworkloadservice.config;

import gym.trainerworkloadservice.model.TrainerWorkload;
import gym.trainerworkloadservice.repository.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkloadDataInitializer implements CommandLineRunner {

    private final TrainerWorkloadRepository repository;

    @Override
    public void run(String... args) {
        repository.save(TrainerWorkload.builder()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .year(2024)
                .month(5)
                .totalDuration(120)
                .build());

        repository.save(TrainerWorkload.builder()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .year(2024)
                .month(6)
                .totalDuration(60)
                .build());

        System.out.println(">> Workload Service: Initial statistics loaded for John.Doe");
    }
}