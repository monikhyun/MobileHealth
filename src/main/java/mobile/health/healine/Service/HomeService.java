package mobile.health.healine.Service;

import mobile.health.healine.Entity.dto.DailyLogDto;
import mobile.health.healine.Entity.dto.GradeDto;
import mobile.health.healine.Entity.dto.ProfileDto;
import org.springframework.stereotype.Service;

@Service
public interface HomeService {
    // 오늘의 활동량 시간 가져오기
    DailyLogDto getDailyLog(String userId);
    // 회원정보 수정
    void updateProfile(String userId, ProfileDto profileDto);
    // 등급 정보 가져오기 (현재 등급, 다음 등급)
    GradeDto getGrade(String userId);
}
