package mobile.health.healine.Entity;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Exercise {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyPart category;

    @Column(nullable = false, length = 50)
    private String exerciseName;

    @Column(name = "exercise_image_path")
    private String exerciseImagePath;

    @Column(length = 120)
    private String description;
}
