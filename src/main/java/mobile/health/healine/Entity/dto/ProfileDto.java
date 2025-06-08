package mobile.health.healine.Entity.dto;


import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class ProfileDto {

    private Gender gender;

    private BigDecimal height;

    private BigDecimal weight;

    private MultipartFile image;

    private Integer age;
}
