package mobile.health.healine.Entity;

import jakarta.persistence.*;
import lombok.*;
import mobile.health.healine.Config.BodyPartConverter;

import java.util.ArrayList;
import java.util.List;


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

    @Convert(converter = BodyPartConverter.class)
    @Column(name = "body_part", columnDefinition = "VARCHAR(10)", nullable = false)
    private BodyPart category;

    @Column(nullable = false, length = 50)
    private String exerciseName;

    @Column(name = "exercise_image_path")
    private String exerciseImagePath;

    @Column(length = 120)
    private String description;

}
