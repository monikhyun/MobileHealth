package mobile.health.healine.Entity.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponseDto {

    private LocalDate startDate;  // 시작일 (일별이면 = endDate와 동일)
    private LocalDate endDate;    // 종료일

    private BigDecimal total;     // 총 운동량 또는 합계 값

    private String periodType;    // "DAILY", "WEEKLY", "MONTHLY" 등 (선택적)
}
