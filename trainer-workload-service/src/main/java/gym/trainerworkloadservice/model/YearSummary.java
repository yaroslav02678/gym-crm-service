package gym.trainerworkloadservice.model;

import java.util.List;

public record YearSummary(
        int year,
        List<MonthSummary> months
) { }