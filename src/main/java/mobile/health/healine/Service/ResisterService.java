package mobile.health.healine.Service;

import mobile.health.healine.Entity.dto.ResisterDto;
import org.springframework.stereotype.Service;

@Service
public interface ResisterService {

    // 회원 정보 저장
    void save(ResisterDto resisterDto);

    // 아이디 검증
    boolean validate(String id);
}
