package gym.crm.service;

import gym.crm.dto.trainee.TraineeShortDTO;
import gym.crm.dto.trainer.request.TrainerRegistrationRequestDTO;
import gym.crm.dto.trainer.request.UpdateTrainerProfileRequestDTO;
import gym.crm.dto.trainer.response.GetTrainerProfileResponseDTO;
import gym.crm.dto.trainer.response.GetTrainerTrainingsListResponseDTO;
import gym.crm.dto.trainer.response.TrainerRegistrationResponseDTO;
import gym.crm.dto.trainer.response.UpdateTrainerProfileResponseDTO;
import gym.crm.model.Trainee;
import gym.crm.model.Trainer;
import gym.crm.model.Training;
import gym.crm.model.TrainingType;
import gym.crm.model.User;
import gym.crm.repository.TrainerRepository;
import gym.crm.repository.TrainingTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final CredentialService credentialService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TrainerRegistrationResponseDTO createTrainer(@Valid TrainerRegistrationRequestDTO request) {
        log.info("Creating Trainer profile for: {} {}", request.getFirstName(), request.getLastName());
        TrainingType specialization = trainingTypeRepository.findById(Long.valueOf(request.getSpecializationId()))
                .orElseThrow(() -> new EntityNotFoundException("Training Type not found with ID: " + request.getSpecializationId()));

        String username = credentialService.generateUsername(request.getFirstName(), request.getLastName());
        String rawPassword = credentialService.generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(request.getFirstName(), request.getLastName(), true);
        user.setUsername(username);
        user.setPassword(encodedPassword);

        Trainer trainer = new Trainer(specialization, user);
        trainerRepository.save(trainer);

        log.info("Trainer created successfully: {}", username);
        return new TrainerRegistrationResponseDTO(username, rawPassword);
    }

    @Transactional(readOnly = true)
    public GetTrainerProfileResponseDTO getTrainerProfile(String username) {
        Trainer trainer = findByUsername(username);
        return mapToGetTrainerProfileResponseDTO(trainer);
    }

    @Transactional
    public UpdateTrainerProfileResponseDTO updateTrainerProfile(String username, @Valid UpdateTrainerProfileRequestDTO request) {
        log.info("Updating profile for trainer: {}", username);
        Trainer trainer = findByUsername(username);
        User user = trainer.getUser();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(request.getIsActive());

        trainerRepository.save(trainer);
        return mapToUpdateTrainerProfileResponseDTO(trainer);
    }

    @Transactional(readOnly = true)
    public List<GetTrainerTrainingsListResponseDTO> getTrainerTrainings(String username, LocalDate periodFrom, LocalDate periodTo, String traineeName) {
        if (!trainerRepository.existsByUsername(username)) {
            throw new EntityNotFoundException("Trainer not found: " + username);
        }
        List<Training> trainings = trainerRepository.getTrainingsByCriteria(username, periodFrom, periodTo, traineeName);

        return trainings.stream()
                .map(this::mapToGetTrainerTrainingsListResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeStatus(String username, boolean isActive) {
        Trainer trainer = findByUsername(username);
        trainer.getUser().setActive(isActive);
        trainerRepository.save(trainer);
        log.info("Trainer status changed to {} for: {}", isActive, username);
    }

    private Trainer findByUsername(String username) {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
    }

    private GetTrainerProfileResponseDTO mapToGetTrainerProfileResponseDTO(Trainer trainer) {
        GetTrainerProfileResponseDTO dto = new GetTrainerProfileResponseDTO();
        dto.setFirstName(trainer.getUser().getFirstName());
        dto.setLastName(trainer.getUser().getLastName());
        dto.setSpecialization(trainer.getSpecialization().getId());
        dto.setActive(trainer.getUser().isActive());

        List<TraineeShortDTO> traineesList = trainer.getTrainees().stream()
                .map(this::mapToTraineeShortDTO)
                .collect(Collectors.toList());
        dto.setTraineesList(traineesList);

        return dto;
    }

    private UpdateTrainerProfileResponseDTO mapToUpdateTrainerProfileResponseDTO(Trainer trainer) {
        UpdateTrainerProfileResponseDTO dto = new UpdateTrainerProfileResponseDTO();
        dto.setUsername(trainer.getUser().getUsername());
        dto.setFirstName(trainer.getUser().getFirstName());
        dto.setLastName(trainer.getUser().getLastName());
        dto.setSpecialization(trainer.getSpecialization().getId());
        dto.setActive(trainer.getUser().isActive());

        List<TraineeShortDTO> traineesList = trainer.getTrainees().stream()
                .map(this::mapToTraineeShortDTO)
                .collect(Collectors.toList());
        dto.setTraineesList(traineesList);

        return dto;
    }

    private GetTrainerTrainingsListResponseDTO mapToGetTrainerTrainingsListResponseDTO(Training training) {
        GetTrainerTrainingsListResponseDTO dto = new GetTrainerTrainingsListResponseDTO();
        dto.setTrainingName(training.getTrainingName());
        dto.setTrainingDate(training.getTrainingDate());
        dto.setTrainingType(training.getTrainingType().getTrainingTypeName());
        dto.setTrainingDuration(training.getTrainingDuration());
        dto.setTraineeName(training.getTrainee().getUser().getFirstName() + " " + training.getTrainee().getUser().getLastName());
        return dto;
    }

    private TraineeShortDTO mapToTraineeShortDTO(Trainee trainee) {
        TraineeShortDTO dto = new TraineeShortDTO();
        dto.setUsername(trainee.getUser().getUsername());
        dto.setFirstName(trainee.getUser().getFirstName());
        dto.setLastName(trainee.getUser().getLastName());
        return dto;
    }
}