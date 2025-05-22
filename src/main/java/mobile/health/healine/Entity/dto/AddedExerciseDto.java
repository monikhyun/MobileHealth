package mobile.health.healine.Entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mobile.health.healine.Entity.BodyPart;

@Builder
@Getter
@Setter
public class AddedExerciseDto {
    private BodyPart bodyPart;
    private String exercise_name;
    private Boolean done;
}
