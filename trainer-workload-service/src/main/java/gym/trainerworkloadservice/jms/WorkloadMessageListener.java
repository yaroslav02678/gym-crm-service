package gym.trainerworkloadservice.jms;

import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import gym.trainerworkloadservice.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadMessageListener {

    private final WorkloadService workloadService;

    @JmsListener(destination = "${app.queue.name}", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(TrainerWorkloadRequest request) {

        String txId = request.getTransactionId();
        if (txId != null) {
            MDC.put("transactionId", txId);
        }

        try {
            log.info("Received async workload update for trainer: {}", request.getTrainerUsername());

            workloadService.processTransaction(request);

            log.info("Successfully processed workload for trainer: {}", request.getTrainerUsername());
        } catch (Exception e) {
            log.error("Error processing workload message", e);
            throw e;
        } finally {
            MDC.remove("transactionId");
        }
    }
}