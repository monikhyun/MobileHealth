package mobile.health.healine.Entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class DailyLogDto {
    private LocalDate date;
    private Integer totalExerciseMinutes;
}
