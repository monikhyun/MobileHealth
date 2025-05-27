package mobile.health.healine.Entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MemberDto {
    private String userid;
    private String username;
    private String grade;
    private String imageUrl;
}
