package mobile.health.healine.Service;

import mobile.health.healine.Entity.dto.InBodyResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface InBodyService {

    // 인바디 기록
    void recordInBody(String userId,InBodyResponseDto inBodyResponseDto);
    // 인바디 목록 불러오기
    List<InBodyResponseDto> getInBodyByUserId(String userId);
    // 인바디 정보 불러오기
    InBodyResponseDto getInBody(String userId, LocalDate localDate);
    // 인바디 정보 수정
    void editInBody(String userId, InBodyResponseDto inBodyResponseDto);
}
