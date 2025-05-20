package mobile.health.healine.Service;

import mobile.health.healine.Entity.dto.DietDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface DietService {
    // 식단 저장
    void dietSave(DietDto dietDto);

    // 식단 조회
    List<DietDto> dietFind(String userId, LocalDate date);
}
