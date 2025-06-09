package roomescape.lock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String lockKey;

    @Builder
    public LockEntity(final Long id, final String lockKey) {
        this.id = id;
        this.lockKey = lockKey;
    }

    public static LockEntity from(final String lockKey) {
        return LockEntity.builder()
                .lockKey(lockKey)
                .build();
    }
}
