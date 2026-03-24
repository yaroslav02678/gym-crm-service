package gym.trainerworkloadservice.controller;

import gym.trainerworkloadservice.dto.TrainerWorkloadRequest;
import gym.trainerworkloadservice.model.TrainerSummary;
import gym.trainerworkloadservice.security.JwtService;
import gym.trainerworkloadservice.service.WorkloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = WorkloadController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
class WorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkloadService workloadService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateWorkload_ReturnsOk() throws Exception {
        TrainerWorkloadRequest request = TrainerWorkloadRequest.builder()
                .trainerUsername("John.Doe")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .actionType(TrainerWorkloadRequest.ActionType.ADD)
                .build();

        doNothing().when(workloadService).processTransaction(any());

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getTrainerWorkload_ReturnsSummary() throws Exception {
        TrainerSummary summary = TrainerSummary.builder()
                .username("John.Doe")
                .firstName("John")
                .build();

        when(workloadService.getTrainerSummary("John.Doe")).thenReturn(summary);

        mockMvc.perform(get("/api/v1/workload/John.Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }
}