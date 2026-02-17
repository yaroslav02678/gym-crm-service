package gym.trainerworkloadservice.model;

import java.util.List;

public record TrainerSummaryResponse(
        String trainerUsername,
        String firstName,
        String lastName,
        boolean trainerStatus,
        List<YearSummary> years
) {}