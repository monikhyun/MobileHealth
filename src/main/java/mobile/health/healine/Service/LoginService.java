package mobile.health.healine.Service;

import mobile.health.healine.Entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {

    // 아이디 검증
    boolean validate(String id, String password);

    Member findMemberByUserId(String userId);
}
