package gym.trainerworkloadservice.service;

import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import gym.trainerworkloadservice.model.*;
import gym.trainerworkloadservice.repository.TrainerSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadService {

    private final TrainerSummaryRepository repository;

    public void processTransaction(TrainerWorkloadRequest request) {
        log.info("Processing workload event for trainer: {}. Action: {}",
                request.getTrainerUsername(), request.getActionType());

        TrainerSummary summary = repository.findByUsername(request.getTrainerUsername())
                .orElseGet(() -> createNewTrainerSummary(request));

        int yearValue = request.getTrainingDate().getYear();
        int monthValue = request.getTrainingDate().getMonthValue();
        long duration = request.getTrainingDuration();

        YearSummary yearRecord = summary.getYears().stream()
                .filter(y -> y.getYear() == yearValue)
                .findFirst()
                .orElseGet(() -> {
                    YearSummary newYear = new YearSummary(yearValue, new ArrayList<>());
                    summary.getYears().add(newYear);
                    return newYear;
                });

        MonthSummary monthRecord = yearRecord.getMonths().stream()
                .filter(m -> m.getMonthValue() == monthValue)
                .findFirst()
                .orElseGet(() -> {
                    MonthSummary newMonth = new MonthSummary(monthValue, 0L);
                    yearRecord.getMonths().add(newMonth);
                    return newMonth;
                });

        if (TrainerWorkloadRequest.ActionType.ADD.equals(request.getActionType())) {
            monthRecord.setDuration(monthRecord.getDuration() + duration);
        } else if (TrainerWorkloadRequest.ActionType.DELETE.equals(request.getActionType())) {
            monthRecord.setDuration(Math.max(0, monthRecord.getDuration() - duration));
        }

        repository.save(summary);
        log.info("Updated workload for trainer {} saved successfully", request.getTrainerUsername());
    }

    private TrainerSummary createNewTrainerSummary(TrainerWorkloadRequest request) {
        log.info("Creating new trainer summary record for: {}", request.getTrainerUsername());
        return TrainerSummary.builder()
                .username(request.getTrainerUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(request.isActive())
                .years(new ArrayList<>())
                .build();
    }

    public TrainerSummary getTrainerSummary(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer workload record not found for: " + username));
    }
}