package gym.crm.service;

import gym.crm.dto.training.request.AddTrainingRequestDTO;
import gym.crm.model.*;
import gym.crm.repository.TraineeRepository;
import gym.crm.repository.TrainerRepository;
import gym.crm.repository.TrainingRepository;
import gym.crm.repository.TrainingTypeRepository;
import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock private TrainingRepository trainingRepository;
    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private JmsTemplate jmsTemplate;
    @InjectMocks private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(trainingService, "workloadQueue", "trainer-workload-queue");
        User user = new User();
        user.setUsername("John.Doe");

        Trainer trainer = new Trainer();
        trainer.setUser(user);
    }

    @Test
    void addTraining_Success() {
        AddTrainingRequestDTO req = new AddTrainingRequestDTO("Trainee", "Trainer", "Yoga Session", LocalDate.now(), 60L);
        User user = new User();
        user.setUsername("John.Doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(new TrainingType(1L, "Yoga"));

        when(traineeRepository.findByUsername("Trainee")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainer));

        trainingService.addTraining(req);

        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void addTraining_TraineeNotFound() {
        AddTrainingRequestDTO req = new AddTrainingRequestDTO("Ghost", "Trainer", "Name", LocalDate.now(), 60L);
        when(traineeRepository.findByUsername("Ghost")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainingService.addTraining(req));
    }

    @Test
    void shouldSendMessageToQueue_whenTrainingAdded() {
        AddTrainingRequestDTO req = new AddTrainingRequestDTO(
                "Trainee",
                "Trainer",
                "Yoga Session",
                LocalDate.now(),
                60L
        );

        User traineeUser = new User("Trainee", "Trainee", true);
        traineeUser.setUsername("Trainee");
        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User("Trainer", "Trainer", true);
        trainerUser.setUsername("Trainer");
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(new TrainingType(1L, "Yoga"));

        when(traineeRepository.findByUsername("Trainee")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("Trainer")).thenReturn(Optional.of(trainer));

        ReflectionTestUtils.setField(trainingService, "workloadQueue", "trainer-workload-queue");

        trainingService.addTraining(req);

        verify(jmsTemplate).convertAndSend(
                eq("trainer-workload-queue"),
                any(TrainerWorkloadRequest.class)
        );
    }
}