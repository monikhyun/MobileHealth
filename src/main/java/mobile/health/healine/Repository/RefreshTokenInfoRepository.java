package mobile.health.healine.Repository;

import mobile.health.healine.Entity.RefreshTokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenInfoRepository
        extends JpaRepository<RefreshTokenInfo, String> {
    // String 은 엔티티의 @Id 타입(username)이 String 이기 때문입니다.
}