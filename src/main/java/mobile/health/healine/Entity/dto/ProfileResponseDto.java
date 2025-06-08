package mobile.health.healine.Entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mobile.health.healine.Entity.Gender;
import mobile.health.healine.Entity.MemberGrade;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class ProfileResponseDto {

    private String name;

    private Gender gender;

    private BigDecimal height;

    private BigDecimal weight;

    private MemberGrade grade;

    private String image;

    private Integer age;
}
