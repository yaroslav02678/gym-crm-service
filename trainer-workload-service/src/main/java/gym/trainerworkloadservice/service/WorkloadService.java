package gym.trainerworkloadservice.service;

import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import gym.trainerworkloadservice.model.MonthSummary;
import gym.trainerworkloadservice.model.TrainerSummaryResponse;
import gym.trainerworkloadservice.model.TrainerWorkload;
import gym.trainerworkloadservice.model.YearSummary;
import gym.trainerworkloadservice.repository.TrainerWorkloadRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkloadService {
    private final TrainerWorkloadRepository repository; // Ваша JPA репозиторій

    @Transactional
    public void processTransaction(TrainerWorkloadRequest request) {
        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();

        // Шукаємо, чи вже є запис за цей місяць/рік
        TrainerWorkload workload = repository.findByUsernameAndYearAndMonth(
                        request.getTrainerUsername(), year, month)
                .orElse(createNewWorkload(request, year, month));

        if ("ADD".equalsIgnoreCase(String.valueOf(request.getActionType()))) {
            workload.setTotalDuration(workload.getTotalDuration() + request.getTrainingDuration());
        } else if ("DELETE".equalsIgnoreCase(String.valueOf(request.getActionType()))) {
            workload.setTotalDuration(Math.max(0, workload.getTotalDuration() - request.getTrainingDuration()));
        }

        repository.save(workload);
    }

    private TrainerWorkload createNewWorkload(TrainerWorkloadRequest request, int year, int month) {
        return TrainerWorkload.builder()
                .username(request.getTrainerUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(request.isActive())
                .year(year)
                .month(month)
                .totalDuration(0)
                .build();
    }

    public TrainerSummaryResponse getTrainerSummary(String username) {
        List<TrainerWorkload> allWorkloads = repository.findAllByUsername(username);

        if (allWorkloads.isEmpty()) {
            throw new EntityNotFoundException("Trainer not found");
        }

        TrainerWorkload first = allWorkloads.get(0);

        List<YearSummary> years = allWorkloads.stream()
                .collect(Collectors.groupingBy(TrainerWorkload::getYear))
                .entrySet().stream()
                .map(yearEntry -> {
                    List<MonthSummary> months = yearEntry.getValue().stream()
                            .map(workload -> new MonthSummary(workload.getMonth(), (int) workload.getTotalDuration()))
                            .collect(Collectors.toList());

                    return new YearSummary(yearEntry.getKey(), months);
                })
                .collect(Collectors.toList());

        return new TrainerSummaryResponse(
                first.getUsername(),
                first.getFirstName(),
                first.getLastName(),
                first.isActive(),
                years
        );
    }
}