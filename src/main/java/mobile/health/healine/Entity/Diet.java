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
public class Diet {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int carbo;

    @Column(nullable = false)
    private int protein;

    @Column(nullable = false)
    private int fat;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal calories;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mealtime mealtime;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
