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

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String exerciseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyPart bodyPart;

    @Column(nullable = false)
    private LocalDate date;

    private Integer setCount;

    private Integer count;

    @Column(precision = 3, scale = 1)
    private BigDecimal weight;

    @Column(nullable = false)
    private Boolean done = false;
}
