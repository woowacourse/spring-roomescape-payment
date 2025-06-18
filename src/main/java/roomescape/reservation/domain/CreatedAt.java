package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import roomescape.reservation.exception.InvalidReservationException;

@Embeddable
public record CreatedAt(
        @Column(name = "created_at", nullable = false)
        LocalDateTime value
) {
    public static CreatedAt from(LocalDateTime createdAt) {
        validate(createdAt);
        return new CreatedAt(createdAt);
    }

    public static CreatedAt now() {
        return new CreatedAt(LocalDateTime.now());
    }

    public boolean isBefore(final CreatedAt comparedCreatedAt) {
        return value.isBefore(comparedCreatedAt.value);
    }

    private static void validate(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new InvalidReservationException("생성 시간은 null일 수 없습니다.");
        }
    }
} 
