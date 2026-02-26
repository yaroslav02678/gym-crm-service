package gym.trainerworkloadservice.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YearSummary {
    private int year;
    private List<MonthSummary> months = new ArrayList<>();
}