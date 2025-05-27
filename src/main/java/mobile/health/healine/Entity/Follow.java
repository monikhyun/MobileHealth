package mobile.health.healine.Entity;

import jakarta.persistence.*;
import lombok.*;
import mobile.health.healine.Entity.Member;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_member_id")
    private Member fromMember;

    @ManyToOne
    @JoinColumn(name = "to_member_id")
    private Member toMember;

    @Enumerated(EnumType.STRING)
    private FollowStatus status;
}
