package mobile.health.healine.Entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mobile.health.healine.Entity.Mealtime;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter@Setter
public class DietDto {
    private String userId;
    private String name;
    private Mealtime mealtime;
    private int carb;
    private int protein;
    private int fat;
    private BigDecimal calories;
    private LocalDate date;
}
