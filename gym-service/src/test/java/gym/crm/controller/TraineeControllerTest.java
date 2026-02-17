package gym.crm.controller;

import gym.crm.dto.trainee.request.TraineeRegistrationRequestDTO;
import gym.crm.dto.trainee.response.TraineeRegistrationResponseDTO;
import gym.crm.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeController traineeController;

    @Test
    void createTrainee() {
        TraineeRegistrationRequestDTO req = new TraineeRegistrationRequestDTO("First", "Last", null, "Addr");
        TraineeRegistrationResponseDTO mockRes = new TraineeRegistrationResponseDTO("User", "Pass");

        when(traineeService.createTrainee(req)).thenReturn(mockRes);

        ResponseEntity<TraineeRegistrationResponseDTO> result = traineeController.createTrainee(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(mockRes, result.getBody());
    }

    @Test
    void deleteTrainee() {
        ResponseEntity<Void> result = traineeController.deleteTrainee("User");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(traineeService).deleteTrainee("User");
    }

    @Test
    void getTraineeProfile() {
        traineeController.getTraineeProfile("User");
        verify(traineeService).getTraineeProfile("User");
    }
}