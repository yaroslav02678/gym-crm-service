package gym.trainerworkloadservice.service;

import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import gym.trainerworkloadservice.model.TrainerSummary;
import gym.trainerworkloadservice.repository.TrainerSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    @Mock
    private TrainerSummaryRepository repository;

    @InjectMocks
    private WorkloadService workloadService;

    private TrainerWorkloadRequest request;

    @BeforeEach
    void setUp() {
        request = TrainerWorkloadRequest.builder()
                .trainerUsername("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2026, 10, 20))
                .trainingDuration(60)
                .actionType(TrainerWorkloadRequest.ActionType.ADD)
                .build();
    }

    @Test
    void processTransaction_NewTrainer_CreatesSummary() {
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        workloadService.processTransaction(request);

        verify(repository).save(argThat(summary -> {
            assertEquals("John.Doe", summary.getUsername());
            assertEquals(1, summary.getYears().size());
            assertEquals(2026, summary.getYears().get(0).getYear());
            assertEquals(60L, summary.getYears().get(0).getMonths().get(0).getDuration());
            return true;
        }));
    }

    @Test
    void processTransaction_ExistingTrainer_AddsDuration() {
        TrainerSummary existingSummary = TrainerSummary.builder()
                .username("John.Doe")
                .years(new ArrayList<>())
                .build();

        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existingSummary));

        workloadService.processTransaction(request);

        request.setTrainingDuration(30);
        workloadService.processTransaction(request);

        verify(repository, times(2)).save(existingSummary);
        assertEquals(90L, existingSummary.getYears().get(0).getMonths().get(0).getDuration());
    }

    @Test
    void processTransaction_DeleteAction_ReducesDuration() {
        TrainerSummary existingSummary = TrainerSummary.builder()
                .username("John.Doe")
                .years(new ArrayList<>())
                .build();

        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existingSummary));

        request.setActionType(TrainerWorkloadRequest.ActionType.ADD);
        request.setTrainingDuration(100);
        workloadService.processTransaction(request);

        request.setActionType(TrainerWorkloadRequest.ActionType.DELETE);
        request.setTrainingDuration(40);
        workloadService.processTransaction(request);

        assertEquals(60L, existingSummary.getYears().get(0).getMonths().get(0).getDuration());
    }

    @Test
    void processTransaction_DeleteAction_DurationNotBelowZero() {
        TrainerSummary existingSummary = TrainerSummary.builder()
                .username("John.Doe")
                .years(new ArrayList<>())
                .build();
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existingSummary));

        request.setActionType(TrainerWorkloadRequest.ActionType.DELETE);
        request.setTrainingDuration(500);
        workloadService.processTransaction(request);

        assertEquals(0L, existingSummary.getYears().get(0).getMonths().get(0).getDuration());
    }

    @Test
    void getTrainerSummary_Success() {
        TrainerSummary summary = TrainerSummary.builder().username("John.Doe").build();
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(summary));

        TrainerSummary result = workloadService.getTrainerSummary("John.Doe");

        assertNotNull(result);
        assertEquals("John.Doe", result.getUsername());
    }

    @Test
    void getTrainerSummary_NotFound_ThrowsException() {
        when(repository.findByUsername("Unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> workloadService.getTrainerSummary("Unknown"));
    }
}