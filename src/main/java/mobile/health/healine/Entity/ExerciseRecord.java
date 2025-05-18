package mobile.health.healine.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "exercise_record")
public class ExerciseRecord {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_record_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int setCount;

    @Column(nullable = false)
    private int count;

    @Column(precision = 3, scale = 1, nullable = false)
    private BigDecimal weight;

    @Column(nullable = false)
    private boolean done;
}
