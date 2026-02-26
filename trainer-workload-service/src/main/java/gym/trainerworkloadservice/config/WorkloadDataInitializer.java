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
        // Очищаємо базу перед завантаженням (корисно для тестів, щоб не дублювати дані)
        repository.deleteAll();

        // 1. Готуємо список місяців для 2025 року
        List<MonthSummary> months2025 = new ArrayList<>();
        months2025.add(new MonthSummary(5, 120L)); // Травень: 120 хв
        months2025.add(new MonthSummary(6, 60L));  // Червень: 60 хв

        // 2. Готуємо список років
        List<YearSummary> years = new ArrayList<>();
        years.add(new YearSummary(2025, months2025));

        // 3. Створюємо фінальний документ тренера згідно з новою схемою (Пункт 1 завдання)
        TrainerSummary johnDoe = TrainerSummary.builder()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .status(true) // Note 4: Boolean type
                .years(years)
                .build();

        // 4. Зберігаємо в MongoDB
        repository.save(johnDoe);

        System.out.println(">> Workload Service: Initial NoSQL document loaded for John.Doe in MongoDB");
    }
}