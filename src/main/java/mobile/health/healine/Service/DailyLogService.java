package mobile.health.healine.Service;

import mobile.health.healine.Entity.dto.DailyLogDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface DailyLogService{
    // 운동 시간 저장
    void save(String userId, LocalDate date, Integer time);
    // 운동 시간 불러오기
    DailyLogDto getDailyLog(String userId, LocalDate date);
}
