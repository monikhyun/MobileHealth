package mobile.health.healine.Repository;

import mobile.health.healine.Entity.Exercise;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.MemberFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberFavoriteRepository extends JpaRepository<MemberFavorite, Long> {
    List<MemberFavorite> findMemberFavoriteByMemberAndExercise(Member byUserId, Exercise byName);

    List<MemberFavorite> findMemberFavoriteByMember(Member byUserId);

    boolean existsByMemberUserIdAndExerciseExerciseName(String userId, String exerciseName);

}
