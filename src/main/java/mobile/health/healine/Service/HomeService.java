package mobile.health.healine.Service;

import mobile.health.healine.Entity.dto.DailyLogDto;
import org.springframework.stereotype.Service;

@Service
public interface HomeService {
    // 오늘의 활동량 시간 가져오기
    DailyLogDto getDailyLog(String userId);
    // 만보기 불러오기

    // 이동한 거리 가져오기
}
