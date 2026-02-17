package gym.trainerworkloadservice.controller;


import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import gym.trainerworkloadservice.model.TrainerSummaryResponse;
import gym.trainerworkloadservice.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workload")
@RequiredArgsConstructor
public class WorkloadController {

    private final WorkloadService workloadService;

    @PostMapping
    public ResponseEntity<Void> updateWorkload(@RequestBody TrainerWorkloadRequest request) {
        workloadService.processTransaction(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerSummaryResponse> getTrainerWorkload(@PathVariable String username) {
        TrainerSummaryResponse summary = workloadService.getTrainerSummary(username);

        return ResponseEntity.ok(summary);
    }
}