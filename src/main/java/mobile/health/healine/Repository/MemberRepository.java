package mobile.health.healine.Repository;

import mobile.health.healine.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUserId(String userId);
    Optional<Member> findMemberByUserId(String userId);
    List<Member> findMemberByUsernameContaining(String username);

    @Query("""
SELECT m FROM Member m
WHERE m.username LIKE %:keyword%
  AND m.userId <> :currentUserId
  AND m NOT IN (
      SELECT f.toMember FROM Follow f
      WHERE f.fromMember.userId = :currentUserId
        AND f.status = mobile.health.healine.Entity.FollowStatus.ACCEPTED
  )
""")
    List<Member> searchExcludingFollowed(@Param("keyword") String keyword,
                                         @Param("currentUserId") String currentUserId);
}
