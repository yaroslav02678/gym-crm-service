package gym.crm.controller;

import gym.crm.dto.training.TrainingTypeDTO;
import gym.crm.dto.training.request.AddTrainingRequestDTO;
import gym.crm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock private TrainingService trainingService;
    @InjectMocks private TrainingController trainingController;

    @Test
    void addTraining() {
        AddTrainingRequestDTO req = new AddTrainingRequestDTO("Trainee", "Trainer", "Name", LocalDate.now(), 60L);

        ResponseEntity<Void> result = trainingController.addTraining(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(trainingService).addTraining(req);
    }

    @Test
    void getTrainingTypes() {
        when(trainingService.getTrainingTypes()).thenReturn(List.of(new TrainingTypeDTO("Yoga", 1L)));

        var result = trainingController.getTrainingTypes();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }
}