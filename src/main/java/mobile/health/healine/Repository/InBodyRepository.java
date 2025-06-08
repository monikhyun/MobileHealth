package mobile.health.healine.Repository;

import mobile.health.healine.Entity.InBody;
import mobile.health.healine.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InBodyRepository extends JpaRepository<InBody, Long> {
    Optional<InBody> findTopByMemberOrderByDateDesc(Member member);
    Optional<InBody> findTopByMemberUserIdOrderByDateDesc(String userId);
    List<InBody> findTop4ByMemberUserIdOrderByDateDesc(String userId);

    int countByMember(Member mjc);
    Optional<InBody> findInBodyByMemberAndDate(Member member, LocalDate date);
    Optional<List<InBody>> findAllByMember(Member member);
}
