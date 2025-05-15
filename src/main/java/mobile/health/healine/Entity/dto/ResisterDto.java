package mobile.health.healine.Entity.dto;

import lombok.Getter;
import lombok.Setter;
import mobile.health.healine.Entity.Gender;

@Getter
@Setter
public class ResisterDto {
    private String userId;
    private String userPassword;
    private Gender userGender;
    private String userMajor;
    private String userEmail;
}
