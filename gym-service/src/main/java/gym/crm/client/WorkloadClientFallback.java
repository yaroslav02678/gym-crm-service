package gym.crm.client;

import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import org.springframework.stereotype.Component;

@Component
public class WorkloadClientFallback implements WorkloadClient {
    @Override
    public void updateWorkload(TrainerWorkloadRequest request) {
        System.out.println("Workload service is unavailable. Request saved to log/queue.");
    }
}