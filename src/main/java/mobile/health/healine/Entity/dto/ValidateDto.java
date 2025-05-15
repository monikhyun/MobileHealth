package mobile.health.healine.Entity.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ValidateDto {
    private boolean newId;
    private String userid;
}
