package mobile.health.healine.Service;

import org.springframework.stereotype.Service;

@Service
public interface LoginService {

    // 아이디 검증
    boolean validate(String id, String password);
}
