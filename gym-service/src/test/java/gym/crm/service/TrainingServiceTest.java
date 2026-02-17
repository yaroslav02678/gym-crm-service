package gym.crm.service;

import gym.crm.dto.training.request.AddTrainingRequestDTO;
import gym.crm.model.Trainee;
import gym.crm.model.Trainer;
import gym.crm.model.Training;
import gym.crm.model.TrainingType;
import gym.crm.repository.TraineeRepository;
import gym.crm.repository.TrainerRepository;
import gym.crm.repository.TrainingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock private TrainingRepository trainingRepository;
    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerRepository trainerRepository;

    @InjectMocks private TrainingService trainingService;

    @Test
    void addTraining_Success() {
        AddTrainingRequestDTO req = new AddTrainingRequestDTO("Trainee", "Trainer", "Yoga Session", LocalDate.now(), 60L);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        trainer.setSpecialization(new TrainingType(1L, "Yoga"));

        when(traineeRepository.findByUsername("Trainee")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("Trainer")).thenReturn(Optional.of(trainer));

        trainingService.addTraining(req);

        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void addTraining_TraineeNotFound() {
        AddTrainingRequestDTO req = new AddTrainingRequestDTO("Ghost", "Trainer", "Name", LocalDate.now(), 60L);
        when(traineeRepository.findByUsername("Ghost")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainingService.addTraining(req));
    }
}