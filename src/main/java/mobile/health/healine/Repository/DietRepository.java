package mobile.health.healine.Repository;

import mobile.health.healine.Entity.Diet;
import mobile.health.healine.Entity.Mealtime;
import mobile.health.healine.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DietRepository extends JpaRepository<Diet, Long> {

    List<Diet> findByMemberIdAndDate(Long memberId, LocalDate date);
    Optional<Diet> findByMemberAndDateAndMealtime(Member member, LocalDate date, Mealtime mealtime);
}
