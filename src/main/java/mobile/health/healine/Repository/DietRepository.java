package mobile.health.healine.Repository;

import mobile.health.healine.Entity.Diet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DietRepository extends JpaRepository<Diet, Long> {

    List<Diet> findByMemberIdAndDate(Long memberId, LocalDate date);
}
