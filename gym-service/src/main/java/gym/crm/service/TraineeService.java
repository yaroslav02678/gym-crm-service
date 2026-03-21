package gym.crm.service;

import gym.crm.dto.trainee.request.TraineeRegistrationRequestDTO;
import gym.crm.dto.trainee.request.UpdateTraineeProfileRequestDTO;
import gym.crm.dto.trainee.response.GetTraineeProfileResponseDTO;
import gym.crm.dto.trainee.response.GetTraineeTrainingsListResponseDTO;
import gym.crm.dto.trainee.response.TraineeRegistrationResponseDTO;
import gym.crm.dto.trainee.response.UpdateTraineeProfileResponseDTO;
import gym.crm.dto.trainer.TrainerShortDTO;
import gym.crm.exeption.TraineeNotFoundException;
import gym.crm.model.Trainee;
import gym.crm.model.Trainer;
import gym.crm.model.Training;
import gym.crm.model.User;
import gym.crm.repository.TraineeRepository;
import gym.crm.repository.TrainerRepository;
import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final CredentialService credentialService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TraineeRegistrationResponseDTO createTrainee(@Valid TraineeRegistrationRequestDTO request) {
        log.info("Creating Trainee profile for: {} {}", request.getFirstName(), request.getLastName());
        String username = credentialService.generateUsername(request.getFirstName(), request.getLastName());
        String rawPassword = credentialService.generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(request.getFirstName(), request.getLastName(), true);
        user.setUsername(username);
        user.setPassword(encodedPassword);

        Trainee trainee = new Trainee(request.getDateOfBirth(), request.getAddress(), user);
        traineeRepository.save(trainee);

        log.info("Trainee created successfully with username: {}", username);
        return new TraineeRegistrationResponseDTO(username, rawPassword);
    }

    @Transactional(readOnly = true)
    public GetTraineeProfileResponseDTO getTraineeProfile(String username) {
        Trainee trainee = findByUsername(username);
        return mapToGetTraineeProfileResponseDTO(trainee);
    }

    @Transactional
    public UpdateTraineeProfileResponseDTO updateTraineeProfile(String username, @Valid UpdateTraineeProfileRequestDTO request) {
        log.info("Updating profile for trainee: {}", username);
        Trainee trainee = findByUsername(username);
        User user = trainee.getUser();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(request.getIsActive());
        trainee.setAddress(request.getAddress());
        trainee.setDateOfBirthday(request.getDateOfBirth());

        traineeRepository.save(trainee);
        return mapToUpdateTraineeProfileResponseDTO(trainee);
    }

    @Transactional
    public void deleteTrainee(String username) {
        if (!traineeRepository.existsByUsername(username)) {
            throw new TraineeNotFoundException("Trainee not found: " + username);
        }
        TrainerWorkloadRequest message = new TrainerWorkloadRequest();
        message.setActionType(TrainerWorkloadRequest.ActionType.DELETE);
        message.setTraineeUsername(username);
        traineeRepository.deleteByUsername(username);
        log.info("Trainee profile deleted: {}", username);
    }

    @Transactional(readOnly = true)
    public List<GetTraineeTrainingsListResponseDTO> getTraineeTrainings(String username, LocalDate periodFrom, LocalDate periodTo, String trainerName, String trainingType) {
        if (!traineeRepository.existsByUsername(username)) {
            throw new TraineeNotFoundException("Trainee not found: " + username);
        }
        List<Training> trainings = traineeRepository.getByCriteria(username, periodFrom, periodTo, trainerName, trainingType);

        return trainings.stream()
                .map(this::mapToGetTraineeTrainingsListResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeStatus(String username, boolean isActive) {
        Trainee trainee = findByUsername(username);
        trainee.getUser().setActive(isActive);
        traineeRepository.save(trainee);
        log.info("Trainee status changed to {} for: {}", isActive, username);
    }

    @Transactional
    public List<TrainerShortDTO> updateTrainersList(String username, List<String> trainerUsernames) {
        log.info("Updating trainer list for trainee: {}", username);
        Trainee trainee = findByUsername(username);
        List<Trainer> newTrainers = trainerRepository.findAllByUserUsernameIn(trainerUsernames);

        trainee.setTrainers(new LinkedHashSet<>(newTrainers));
        traineeRepository.save(trainee);

        return newTrainers.stream()
                .map(this::mapToTrainerShortDTO)
                .collect(Collectors.toList());
    }

    private Trainee findByUsername(String username) {
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + username));
    }

    @Transactional(readOnly = true)
    public List<TrainerShortDTO> getNotAssignedActiveTrainers(String username) {
        log.info("Getting unassigned active trainers for trainee: {}", username);

        if (!traineeRepository.existsByUsername(username)) {
            throw new TraineeNotFoundException("Trainee not found: " + username);
        }

        List<Trainer> trainers = trainerRepository.getUnassignedActiveTrainers(username);

        return trainers.stream()
                .map(this::mapToTrainerShortDTO)
                .collect(Collectors.toList());
    }

    private GetTraineeProfileResponseDTO mapToGetTraineeProfileResponseDTO(Trainee trainee) {
        GetTraineeProfileResponseDTO dto = new GetTraineeProfileResponseDTO();
        dto.setFirstName(trainee.getUser().getFirstName());
        dto.setLastName(trainee.getUser().getLastName());
        dto.setDateOfBirth(trainee.getDateOfBirthday());
        dto.setAddress(trainee.getAddress());
        dto.setActive(trainee.getUser().isActive());

        List<TrainerShortDTO> trainersList = trainee.getTrainers().stream()
                .map(this::mapToTrainerShortDTO)
                .collect(Collectors.toList());
        dto.setTrainersList(trainersList);

        return dto;
    }

    private UpdateTraineeProfileResponseDTO mapToUpdateTraineeProfileResponseDTO(Trainee trainee) {
        UpdateTraineeProfileResponseDTO dto = new UpdateTraineeProfileResponseDTO();
        dto.setUsername(trainee.getUser().getUsername());
        dto.setFirstName(trainee.getUser().getFirstName());
        dto.setLastName(trainee.getUser().getLastName());
        dto.setDateOfBirth(trainee.getDateOfBirthday());
        dto.setActive(trainee.getUser().isActive());

        List<TrainerShortDTO> trainersList = trainee.getTrainers().stream()
                .map(this::mapToTrainerShortDTO)
                .collect(Collectors.toList());
        dto.setTrainersList(trainersList);

        return dto;
    }

    private GetTraineeTrainingsListResponseDTO mapToGetTraineeTrainingsListResponseDTO(Training training) {
        GetTraineeTrainingsListResponseDTO dto = new GetTraineeTrainingsListResponseDTO();
        dto.setTrainingName(training.getTrainingName());
        dto.setTrainingDate(training.getTrainingDate());
        dto.setTrainingType(training.getTrainingType().getTrainingTypeName());
        dto.setTrainingDuration(training.getTrainingDuration());
        dto.setTrainerName(training.getTrainer().getUser().getFirstName() + " " + training.getTrainer().getUser().getLastName());
        return dto;
    }

    private TrainerShortDTO mapToTrainerShortDTO(Trainer trainer) {
        TrainerShortDTO dto = new TrainerShortDTO();
        dto.setUsername(trainer.getUser().getUsername());
        dto.setFirstName(trainer.getUser().getFirstName());
        dto.setLastName(trainer.getUser().getLastName());
        dto.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
        return dto;
    }
}