package mobile.health.healine.Repository;

import mobile.health.healine.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUserId(String userId);
    Optional<Member> findMemberByUserId(String userId);
    List<Member> findMemberByUsernameContaining(String username);
}
