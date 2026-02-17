package gym.crm.controller;

import gym.crm.dto.trainer.request.TrainerRegistrationRequestDTO;
import gym.crm.dto.trainer.request.UpdateTrainerProfileRequestDTO;
import gym.crm.dto.trainer.response.GetTrainerProfileResponseDTO;
import gym.crm.dto.trainer.response.GetTrainerTrainingsListResponseDTO;
import gym.crm.dto.trainer.response.TrainerRegistrationResponseDTO;
import gym.crm.dto.trainer.response.UpdateTrainerProfileResponseDTO;
import gym.crm.service.TrainerService;
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
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainer Controller", description = "Endpoints for managing Trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping
    @Operation(summary = "Register Trainer")
    public ResponseEntity<TrainerRegistrationResponseDTO> registerTrainer(
            @RequestBody @Valid TrainerRegistrationRequestDTO request
    ) {
        TrainerRegistrationResponseDTO response = trainerService.createTrainer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get Trainer Profile")
    public ResponseEntity<GetTrainerProfileResponseDTO> getTrainerProfile(
            @PathVariable("username") String username
    ) {
        GetTrainerProfileResponseDTO response = trainerService.getTrainerProfile(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update Trainer Profile")
    public ResponseEntity<UpdateTrainerProfileResponseDTO> updateTrainerProfile(
            @PathVariable("username") String username,
            @RequestBody @Valid UpdateTrainerProfileRequestDTO request
    ) {
        UpdateTrainerProfileResponseDTO response = trainerService.updateTrainerProfile(username, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get Trainer Trainings List")
    public ResponseEntity<List<GetTrainerTrainingsListResponseDTO>> getTrainerTrainings(
            @PathVariable("username") String username,
            @RequestParam(value = "periodFrom", required = false) LocalDate periodFrom,
            @RequestParam(value = "periodTo", required = false) LocalDate periodTo,
            @RequestParam(value = "traineeName", required = false) String traineeName
    ) {
        List<GetTrainerTrainingsListResponseDTO> response = trainerService.getTrainerTrainings(
                username, periodFrom, periodTo, traineeName
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}")
    @Operation(summary = "Activate/Deactivate Trainer")
    public ResponseEntity<Void> changeStatus(
            @PathVariable("username") String username,
            @RequestParam("isActive") boolean isActive
    ) {
        trainerService.changeStatus(username, isActive);
        return ResponseEntity.ok().build();
    }
}