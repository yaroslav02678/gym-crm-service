package gym.crm.controller;

import gym.crm.dto.trainer.request.TrainerRegistrationRequestDTO;
import gym.crm.dto.trainer.request.UpdateTrainerProfileRequestDTO;
import gym.crm.dto.trainer.response.GetTrainerProfileResponseDTO;
import gym.crm.dto.trainer.response.TrainerRegistrationResponseDTO;
import gym.crm.dto.trainer.response.UpdateTrainerProfileResponseDTO;
import gym.crm.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock private TrainerService trainerService;
    @InjectMocks private TrainerController trainerController;

    @Test
    void registerTrainer() {
        TrainerRegistrationRequestDTO req = new TrainerRegistrationRequestDTO("John", "Doe", 1L);
        TrainerRegistrationResponseDTO res = new TrainerRegistrationResponseDTO("John.Doe", "pass");

        when(trainerService.createTrainer(req)).thenReturn(res);

        ResponseEntity<TrainerRegistrationResponseDTO> result = trainerController.registerTrainer(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void getTrainerProfile() {
        GetTrainerProfileResponseDTO res = new GetTrainerProfileResponseDTO();
        when(trainerService.getTrainerProfile("User")).thenReturn(res);

        ResponseEntity<GetTrainerProfileResponseDTO> result = trainerController.getTrainerProfile("User");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(res, result.getBody());
    }

    @Test
    void updateTrainerProfile() {
        UpdateTrainerProfileRequestDTO req = new UpdateTrainerProfileRequestDTO("John.Doe", "F", "L", 1L, true);
        UpdateTrainerProfileResponseDTO res = new UpdateTrainerProfileResponseDTO();

        when(trainerService.updateTrainerProfile(eq("User"), any())).thenReturn(res);

        var result = trainerController.updateTrainerProfile("User", req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getTrainerTrainings() {
        when(trainerService.getTrainerTrainings("User", null, null, null))
                .thenReturn(Collections.emptyList());

        var result = trainerController.getTrainerTrainings("User", null, null, null);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void changeStatus() {
        var result = trainerController.changeStatus("User", true);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(trainerService).changeStatus("User", true);
    }
}