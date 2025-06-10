package mobile.health.healine.Entity.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import mobile.health.healine.Entity.InBody;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InBodyResponseDto {

    private BigDecimal weight;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private BigDecimal SMM;

    private BigDecimal LBM;

    private BigDecimal BMI;

    private BigDecimal fat_percent;

    public InBodyResponseDto toDto(InBody inBody) {
        return InBodyResponseDto.builder()
                .weight(inBody.getWeight())
                .date(inBody.getDate())
                .SMM(inBody.getSMM())
                .LBM(inBody.getLBM())
                .BMI(inBody.getBMI())
                .fat_percent(inBody.getFat_percent())
                .build();
    }
}
