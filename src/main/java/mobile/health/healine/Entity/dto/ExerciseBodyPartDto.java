package mobile.health.healine.Entity.dto;

import lombok.*;
import mobile.health.healine.Entity.BodyPart;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseBodyPartDto {
    private BodyPart bodyPart;
    private Integer totalVolume;
    private LocalDate startDate;  // 시작일 (일별이면 = endDate와 동일)
    private LocalDate endDate;    // 종료일
}
