package mobile.health.healine.Entity.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mobile.health.healine.Entity.MemberGrade;

@Builder
@Getter
@Setter
public class GradeDto {
    private MemberGrade memberGrade;
    private MemberGrade nextGrade;
    private Long count;
}
