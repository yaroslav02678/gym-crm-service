package gym.trainerworkloadservice;

import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import gym.trainerworkloadservice.model.MonthSummary;
import gym.trainerworkloadservice.model.TrainerSummary;
import gym.trainerworkloadservice.model.YearSummary;
import gym.trainerworkloadservice.repository.TrainerSummaryRepository;
import gym.trainerworkloadservice.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    @Mock
    private TrainerSummaryRepository repository;

    @InjectMocks
    private WorkloadService workloadService;

    private TrainerWorkloadRequest addRequest;

    @BeforeEach
    void setUp() {
        addRequest = TrainerWorkloadRequest.builder()
                .trainerUsername("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 5, 10))
                .trainingDuration(60)
                .actionType(TrainerWorkloadRequest.ActionType.ADD)
                .build();
    }

    @Test
    void testProcessTransaction_WhenTrainerDoesNotExist_ShouldCreateNew() {
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        workloadService.processTransaction(addRequest);

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(repository).save(captor.capture());

        TrainerSummary saved = captor.getValue();
        assertEquals("John.Doe", saved.getUsername());
        assertEquals(1, saved.getYears().size());
    }

    @Test
    void testProcessTransaction_WhenTrainerExists_ShouldUpdateDuration() {
        TrainerSummary existingSummary = TrainerSummary.builder()
                .username("John.Doe")
                .years(new ArrayList<>())
                .build();
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existingSummary));

        workloadService.processTransaction(addRequest);

        verify(repository).save(existingSummary);
        assertEquals(60L, existingSummary.getYears().get(0).getMonths().get(0).getDuration());
    }

    @Test
    void testProcessTransaction_DeleteAction_ShouldDecreaseDuration() {
        TrainerWorkloadRequest deleteRequest = addRequest;
        deleteRequest.setActionType(TrainerWorkloadRequest.ActionType.DELETE);
        deleteRequest.setTrainingDuration(40);

        workloadService.processTransaction(addRequest);

        TrainerSummary summaryWithHours = createSummaryWithHours("John.Doe", 2025, 5, 100L);
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(summaryWithHours));

        workloadService.processTransaction(deleteRequest);

        assertEquals(60L, summaryWithHours.getYears().get(0).getMonths().get(0).getDuration());
    }

    @Test
    void testProcessTransaction_DeleteAction_ShouldNotGoBelowZero() {
        TrainerSummary summaryWithHours = createSummaryWithHours("John.Doe", 2025, 5, 10L);
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(summaryWithHours));

        addRequest.setActionType(TrainerWorkloadRequest.ActionType.DELETE);
        addRequest.setTrainingDuration(50);

        workloadService.processTransaction(addRequest);

        assertEquals(0L, summaryWithHours.getYears().get(0).getMonths().get(0).getDuration());
    }

    private TrainerSummary createSummaryWithHours(String username, int year, int month, long duration) {
        MonthSummary monthSummary = new MonthSummary(month, duration);
        List<MonthSummary> months = new ArrayList<>();
        months.add(monthSummary);

        YearSummary yearSummary = new YearSummary(year, months);
        List<YearSummary> years = new ArrayList<>();
        years.add(yearSummary);

        return TrainerSummary.builder()
                .username(username)
                .firstName("John")
                .lastName("Doe")
                .status(true)
                .years(years)
                .build();
    }
}