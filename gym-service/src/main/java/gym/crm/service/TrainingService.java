package gym.crm.service;

import gym.crm.client.WorkloadClient;
import gym.crm.dto.training.TrainingTypeDTO;
import gym.crm.dto.training.request.AddTrainingRequestDTO;
import gym.crm.model.Trainee;
import gym.crm.model.Trainer;
import gym.crm.model.Training;
import gym.crm.model.TrainingType;
import gym.crm.repository.TraineeRepository;
import gym.crm.repository.TrainerRepository;
import gym.crm.repository.TrainingRepository;
import gym.crm.repository.TrainingTypeRepository;
import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final WorkloadClient workloadClient;

    @Transactional
    public void addTraining(@Valid AddTrainingRequestDTO request) {
        log.info("Adding training: '{}' for Trainee: {} and Trainer: {}",
                request.getTrainingName(), request.getTraineeUsername(), request.getTrainerUsername());

        Trainee trainee = traineeRepository.findByUsername(request.getTraineeUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + request.getTraineeUsername()));

        Trainer trainer = trainerRepository.findByUsername(request.getTrainerUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + request.getTrainerUsername()));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(request.getTrainingName());
        training.setTrainingDate(request.getTrainingDate());
        training.setTrainingDuration(Math.toIntExact(request.getTrainingDuration()));
        training.setTrainingType(trainer.getSpecialization());

        trainingRepository.save(training);
        log.info("Training added successfully");
        TrainerWorkloadRequest request1 = new TrainerWorkloadRequest(
                training.getTrainer().getUser().getUsername(),
                training.getTrainer().getUser().getFirstName(),
                training.getTrainer().getUser().getLastName(),
                training.getTrainer().getUser().isActive(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                TrainerWorkloadRequest.ActionType.ADD
        );

        workloadClient.updateWorkload(request1);
    }

    @Transactional(readOnly = true)
    public List<TrainingTypeDTO> getTrainingTypes() {
        return trainingTypeRepository.findAll().stream()
                .map(this::mapToTrainingTypeDTO)
                .collect(Collectors.toList());
    }

    private TrainingTypeDTO mapToTrainingTypeDTO(TrainingType type) {
        TrainingTypeDTO dto = new TrainingTypeDTO();
        dto.setTrainingTypeId(type.getId());
        dto.setTrainingType(type.getTrainingTypeName());
        return dto;
    }
}