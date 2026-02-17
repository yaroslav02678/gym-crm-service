package gym.crm.controller;

import gym.crm.dto.trainee.request.*;
import gym.crm.dto.trainee.response.GetTraineeProfileResponseDTO;
import gym.crm.dto.trainee.response.GetTraineeTrainingsListResponseDTO;
import gym.crm.dto.trainee.response.TraineeRegistrationResponseDTO;
import gym.crm.dto.trainee.response.UpdateTraineeProfileResponseDTO;
import gym.crm.dto.trainer.TrainerShortDTO;
import gym.crm.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainee Controller", description = "Endpoints for managing Trainees")
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping
    @Operation(summary = "Reqister Trainee", description = "Creates a new trainee profile and returns generated credentials")
    public ResponseEntity<TraineeRegistrationResponseDTO> createTrainee(
            @RequestBody @Valid TraineeRegistrationRequestDTO request
    ){
        TraineeRegistrationResponseDTO response = traineeService.createTrainee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get Trainee Profile", description = "Retrieves trainee profile details")
    public ResponseEntity<GetTraineeProfileResponseDTO> getTraineeProfile(
            @PathVariable("username") String username
    ) {
        GetTraineeProfileResponseDTO response = traineeService.getTraineeProfile(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update Trainee Profile", description = "Updates trainee details")
    public ResponseEntity<UpdateTraineeProfileResponseDTO> updateTraineeProfile(
            @PathVariable("username") String username,
            @RequestBody @Valid UpdateTraineeProfileRequestDTO request
    ) {
        UpdateTraineeProfileResponseDTO response = traineeService.updateTraineeProfile(username, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{username}")
    @Operation(summary = "Delete Trainee", description = "Hard deletes trainee and associated trainings")
    public ResponseEntity<Void> deleteTrainee(
        @PathVariable("username") String username
    ) {
        traineeService.deleteTrainee(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get Trainee Training", description = "Retrieves trainings with optional filtering")
    public ResponseEntity<List<GetTraineeTrainingsListResponseDTO>> getTraineeTrainingList(
            @RequestParam("username") String username,
            @RequestParam(value = "periodFrom", required = false) LocalDate periodFrom,
            @RequestParam(value = "periodTo", required = false) LocalDate periodTo,
            @RequestParam(value = "trainerName", required = false) String trainerName,
            @RequestParam(value = "trainingType", required = false) String trainingType
    ) {
        List<GetTraineeTrainingsListResponseDTO> response = traineeService.getTraineeTrainings(
                username, periodFrom, periodTo, trainerName, trainingType
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}")
    @Operation(summary = "Change Trainee Status", description = "Activates or Deactivates the trainee")
    public ResponseEntity<Void> changeTraineeStatus(
            @PathVariable("username") String username,
            @RequestParam("isActive") boolean isActive
    ) {
        traineeService.changeStatus(username, isActive);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary = "Update Trainer List", description = "Updates the list of trainers assigned to the trainee")
    public ResponseEntity<List<TrainerShortDTO>> updateTrainersList(
            @PathVariable("username") String username,
            @RequestBody List<String> trainerUsernames
    ) {
        List<TrainerShortDTO> response = traineeService.updateTrainersList(username, trainerUsernames);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trainers/not-assigned")
    @Operation(summary = "Get not-assigned Trainer List", description = "Get the free trainers")
    public ResponseEntity<List<TrainerShortDTO>> getNotAssignedTrainers(
            @RequestParam("username") String username
    ) {
        List<TrainerShortDTO> response = traineeService.getNotAssignedActiveTrainers(username);
        return ResponseEntity.ok(response);
    }
}