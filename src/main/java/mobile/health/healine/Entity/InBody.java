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
public class InBody {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 3, scale = 1)
    private BigDecimal weight;

    private LocalDate date;

    @Column(precision = 2, scale = 1)
    private BigDecimal SMM;

    @Column(precision = 3, scale = 1)
    private BigDecimal LBM;

    @Column(precision = 2, scale = 1)
    private BigDecimal BMI;

    @Column(precision = 2, scale = 1)
    private BigDecimal fat_percent;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
