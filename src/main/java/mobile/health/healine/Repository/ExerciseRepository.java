package mobile.health.healine.Repository;

import mobile.health.healine.Entity.BodyPart;
import mobile.health.healine.Entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Exercise findByName(String name);

    List<Exercise> findByNameAndCategory(String exerciseName, BodyPart bodyPart);
}
