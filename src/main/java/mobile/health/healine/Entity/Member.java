package mobile.health.healine.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Member {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    private String userId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 20)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ROLE role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(precision = 4, scale = 1)
    private BigDecimal height;

    @Column(precision = 4, scale = 1)
    private BigDecimal weight;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private String grade;

    @OneToMany(mappedBy = "member")
    private List<Diet> diets;

    @OneToMany(mappedBy = "fromMember", cascade = CascadeType.ALL)
    private List<Follow> following;

    @OneToMany(mappedBy = "toMember", cascade = CascadeType.ALL)
    private List<Follow> followers;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();
}
