package gym.crm.controller;

import gym.crm.dto.training.TrainingTypeDTO;
import gym.crm.dto.training.request.AddTrainingRequestDTO;
import gym.crm.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@Tag(name = "Training Controller", description = "Endpoints for managing Trainings and Types")
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    @Operation(summary = "Add Training", description = "Creates a new training session")
    public ResponseEntity<Void> addTraining(
            @RequestBody @Valid AddTrainingRequestDTO request
    ) {
        trainingService.addTraining(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/types")
    @Operation(summary = "Get Training Types", description = "Returns a list of available training types")
    public ResponseEntity<List<TrainingTypeDTO>> getTrainingTypes() {
        List<TrainingTypeDTO> types = trainingService.getTrainingTypes();
        return ResponseEntity.ok(types);
    }
}