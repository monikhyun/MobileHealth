package mobile.health.healine.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "refresh_tokens")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenInfo {

    /**
     * 사용자 식별자(Username 또는 userId 등)를 PK로 사용
     */
    @Id
    @Column(name = "username", nullable = false, updatable = false)
    private String username;

    /**
     * 실제 Refresh Token 값
     */
    @Column(name = "refresh_token", nullable = false, length = 512)
    private String refreshToken;
}