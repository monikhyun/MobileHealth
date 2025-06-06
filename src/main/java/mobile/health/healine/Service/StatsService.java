package mobile.health.healine.Service;

import mobile.health.healine.Entity.dto.InBodyResponseDto;
import mobile.health.healine.Entity.dto.StatsResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface StatsService {
    // 일별 운동 볼륨량
    List<StatsResponseDto> getDailyStats(String userId);
    // 주간별 운동 볼륨량
    List<StatsResponseDto> getWeeklyStats(String userId, LocalDate date);
    // 월별 운동 볼륨량
    List<StatsResponseDto> getMonthlyStats(String userId, LocalDate date);
    // 최신 인바디 전체 데이터
    InBodyResponseDto getInBodyResponse(String userId);
    // 최근 4개 체중 데이터
    List<InBodyResponseDto> getWeight(String userId);
    // 최근 4개 골격근량 데이터
    List<InBodyResponseDto> getSMM(String userId);
    // 최근 4개 제지방량 데이터
    List<InBodyResponseDto> getLBM(String userId);
    // 최근 4개 BMI 데이터
    List<InBodyResponseDto> getBMI(String userId);
    // 최근 4개 체지방률 데이터
    List<InBodyResponseDto> getFatPercent(String userId);

}
