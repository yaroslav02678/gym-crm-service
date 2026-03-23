package gym.crm.service;

import gym.crm.dto.trainer.request.TrainerRegistrationRequestDTO;
import gym.crm.dto.trainer.request.UpdateTrainerProfileRequestDTO;
import gym.crm.dto.trainer.response.GetTrainerProfileResponseDTO;
import gym.crm.model.*;
import gym.crm.repository.TrainerRepository;
import gym.crm.repository.TrainingTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private CredentialService credentialService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private TrainerService trainerService;

    @Test
    void createTrainer() {
        TrainerRegistrationRequestDTO req = new TrainerRegistrationRequestDTO("John", "Doe", 1L);
        TrainingType type = new TrainingType(1L, "Yoga");

        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(type));
        when(credentialService.generateUsername("John", "Doe")).thenReturn("John.Doe");
        when(credentialService.generateRandomPassword()).thenReturn("pass");
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");

        var res = trainerService.createTrainer(req);

        assertEquals("John.Doe", res.getUsername());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void getTrainerProfile() {
        TrainingType type = new TrainingType(1L, "Yoga");
        User user = new User("John", "Doe", true);
        user.setUsername("John.Doe");
        Trainer trainer = new Trainer(type, user);
        trainer.setTrainees(new LinkedHashSet<>());

        when(trainerRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainer));

        GetTrainerProfileResponseDTO res = trainerService.getTrainerProfile("John.Doe");

        assertEquals(1L, res.getSpecialization());
        assertEquals("John", res.getFirstName());
    }

    @Test
    void getTrainerTrainings_ShouldMapCorrectly() {
        User traineeUser = new User("Trainee", "Last", true);
        Trainee trainee = new Trainee(LocalDate.now(), "Addr", traineeUser);

        User trainerUser = new User("Trainer", "Last", true);
        TrainingType type = new TrainingType(1L, "Yoga");
        Trainer trainer = new Trainer(type, trainerUser);

        Training training = new Training();
        training.setTrainingName("Training 1");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);
        training.setTrainingType(type);
        training.setTrainee(trainee);
        training.setTrainer(trainer);

        when(trainerRepository.existsByUsername("Trainer")).thenReturn(true);
        when(trainerRepository.getTrainingsByCriteria(any(), any(), any(), any()))
                .thenReturn(List.of(training));

        var result = trainerService.getTrainerTrainings("Trainer", null, null, null);

        assertEquals(1, result.size());
        assertEquals("Trainee Last", result.get(0).getTraineeName());
        assertEquals("Yoga", result.get(0).getTrainingType());
    }

    @Test
    void updateTrainerProfile_ShouldMapCorrectly() {
        User user = new User("Old", "Name", true);
        user.setUsername("User");
        TrainingType type = new TrainingType(1L, "Yoga");
        Trainer trainer = new Trainer(type, user);
        trainer.setTrainees(new LinkedHashSet<>());

        when(trainerRepository.findByUsername("User")).thenReturn(Optional.of(trainer));

        UpdateTrainerProfileRequestDTO req = new UpdateTrainerProfileRequestDTO("Old.Name", "New", "Name", 2L, true); // Check your DTO constructor

        var res = trainerService.updateTrainerProfile("User", req);

        assertEquals("New", res.getFirstName());
        assertEquals(1L, res.getSpecialization());
    }
}