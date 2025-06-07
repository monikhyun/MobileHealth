package mobile.health.healine.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class DailyExerciseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Integer totalExerciseMinutes;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
