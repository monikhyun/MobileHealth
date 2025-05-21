package mobile.health.healine.Entity.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
public class ExerciseRecordDto {
    private Integer setCount;
    private Integer count;
    private BigDecimal weight;
    private Boolean done;
    private LocalDate date;
}
