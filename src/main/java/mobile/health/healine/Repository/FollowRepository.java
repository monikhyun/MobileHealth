package mobile.health.healine.Repository;

import mobile.health.healine.Entity.Follow;
import mobile.health.healine.Entity.FollowStatus;
import mobile.health.healine.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FollowRepository extends JpaRepository<Follow, Long> {
    // 날 팔로우하는 사람들 (팔로워들)
    @Query("SELECT f.fromMember FROM Follow f WHERE f.toMember = :toMember AND f.status = 'ACCEPTED'")
    List<Member> findFollowers(@Param("toMember") Member toMember);

    // 내가 팔로우하는 사람들 (팔로잉들)
    @Query("SELECT f.toMember FROM Follow f WHERE f.fromMember = :fromMember AND f.status = 'ACCEPTED'")
    List<Member> findFollowings(@Param("fromMember") Member fromMember);

    @Query("SELECT f.fromMember FROM Follow f WHERE f.toMember = :toMember AND f.status = 'REQUESTED'")
    List<Member> findFollowRequests(Member toMember);

    @Query("SELECT f.fromMember FROM Follow f WHERE f.fromMember = :fromMember  AND f.toMember = :toMember AND f.status = 'REQUESTED'")
    Follow findByFromMemberAndToMember(Member fromMember, Member toMember);
}
