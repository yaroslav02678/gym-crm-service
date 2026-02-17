package gym.crm.client;

import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "trainer-workload-service",
             fallback = WorkloadClientFallback.class)
public interface WorkloadClient {
    @PostMapping("/api/v1/workload")
    void updateWorkload(@RequestBody TrainerWorkloadRequest request);
}