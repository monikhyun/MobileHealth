package mobile.health.healine.Entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mobile.health.healine.Entity.Diet;
import mobile.health.healine.Entity.Mealtime;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter@Setter
public class DietDto {
    private Long id;
    private String userId;
    private String name;
    private Mealtime mealtime;
    private Integer carb;
    private Integer protein;
    private Integer fat;
    private BigDecimal calories;
    private LocalDate date;

    public static DietDto fromEntity(Diet diet) {
        return DietDto.builder()
                .id(diet.getId())
                .userId(diet.getMember().getUsername()) // 또는 getUserid() 등 Member 클래스에 따라 변경
                .name(diet.getName())
                .mealtime(diet.getMealtime())
                .carb(diet.getCarbo())
                .protein(diet.getProtein())
                .fat(diet.getFat())
                .calories(diet.getCalories())
                .date(diet.getDate())
                .build();
    }
}
