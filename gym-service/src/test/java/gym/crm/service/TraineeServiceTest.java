package gym.crm.service;

import gym.crm.config.JmsConfig;
import gym.crm.dto.trainee.request.TraineeRegistrationRequestDTO;
import gym.crm.dto.trainee.request.UpdateTraineeProfileRequestDTO;
import gym.crm.dto.trainee.response.GetTraineeProfileResponseDTO;
import gym.crm.dto.trainee.response.TraineeRegistrationResponseDTO;
import gym.crm.exeption.TraineeNotFoundException;
import gym.crm.model.Trainee;
import gym.crm.model.Trainer;
import gym.crm.model.TrainingType;
import gym.crm.model.User;
import gym.crm.repository.TraineeRepository;
import gym.crm.repository.TrainerRepository;
import gym.crm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private CredentialService credentialService;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private TraineeService traineeService;

    @Test
    void createTrainee() {
        TraineeRegistrationRequestDTO req = new TraineeRegistrationRequestDTO("Ivan", "Ivanov", LocalDate.now(), "Addr");
        when(credentialService.generateUsername(any(), any())).thenReturn("Ivan.Ivanov");
        when(credentialService.generateRandomPassword()).thenReturn("pass");
        when(passwordEncoder.encode(any())).thenReturn("hashed_pass");

        TraineeRegistrationResponseDTO res = traineeService.createTrainee(req);

        assertNotNull(res);
        assertEquals("Ivan.Ivanov", res.getUsername());
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void getTraineeProfile_Success() {
        User user = new User("Ivan", "Ivanov", true);
        user.setUsername("Ivan.Ivanov");
        Trainee trainee = new Trainee(LocalDate.now(), "Addr", user);
        trainee.setTrainers(new LinkedHashSet<>());

        when(traineeRepository.findByUsername("Ivan.Ivanov")).thenReturn(Optional.of(trainee));

        GetTraineeProfileResponseDTO res = traineeService.getTraineeProfile("Ivan.Ivanov");

        assertEquals("Ivan", res.getFirstName());
        assertEquals("Addr", res.getAddress());
    }

    @Test
    void getTraineeProfile_NotFound() {
        when(traineeRepository.findByUsername("Ghost")).thenReturn(Optional.empty());
        assertThrows(TraineeNotFoundException.class, () -> traineeService.getTraineeProfile("Ghost"));
    }

    @Test
    void updateTraineeProfile() {
        User user = new User("Old", "Name", true);
        Trainee trainee = new Trainee(LocalDate.now(), "OldAddr", user);
        trainee.setTrainers(new LinkedHashSet<>());

        when(traineeRepository.findByUsername("User")).thenReturn(Optional.of(trainee));

        UpdateTraineeProfileRequestDTO req = new UpdateTraineeProfileRequestDTO("Old.Name", "New", "Name", LocalDate.now(), "address", true);

        var res = traineeService.updateTraineeProfile("User", req);

        assertEquals("New", res.getFirstName());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void deleteTrainee() {
        when(traineeRepository.existsByUsername("User")).thenReturn(true);
        traineeService.deleteTrainee("User");
        verify(traineeRepository).deleteByUsername("User");
    }

    @Test
    void getNotAssignedActiveTrainers_ShouldMapCorrectly() {
        User trainerUser = new User("Trainer", "One", true);
        trainerUser.setUsername("Trainer.One");
        TrainingType type = new TrainingType(2L, "Box");
        Trainer trainer = new Trainer(type, trainerUser);

        when(traineeRepository.existsByUsername("Trainee")).thenReturn(true);
        when(trainerRepository.getUnassignedActiveTrainers("Trainee")).thenReturn(List.of(trainer));

        var result = traineeService.getNotAssignedActiveTrainers("Trainee");

        assertEquals(1, result.size());
        assertEquals("Box", result.get(0).getSpecialization()); // Covers mapper lines
        assertEquals("Trainer.One", result.get(0).getUsername());
    }

    @Test
    void updateTrainersList_ShouldMapCorrectly() {
        User user = new User("Trainee", "User", true);
        Trainee trainee = new Trainee(LocalDate.now(), "Addr", user);

        User trainerUser = new User("Trainer", "Two", true);
        Trainer trainer = new Trainer(new TrainingType(1L, "Gym"), trainerUser);

        when(traineeRepository.findByUsername("Trainee")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllByUserUsernameIn(anyList())).thenReturn(List.of(trainer));

        var result = traineeService.updateTrainersList("Trainee", List.of("Trainer.Two"));

        assertEquals(1, result.size());
        assertEquals("Trainer", result.get(0).getFirstName());
    }
}