package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record ReservationStatus(
        @Column(name = "reservation_status", nullable = false)
        @Enumerated(value = EnumType.STRING)
        Status value
) {
    public enum Status {
        REQUESTED,
        CONFIRMED,
        FAILED;

        public boolean isFinished() {
            return this == CONFIRMED || this == FAILED;
        }

        public boolean isConfirmed() {
            return this == CONFIRMED;
        }
    }

    public static ReservationStatus requested() {
        return new ReservationStatus(Status.REQUESTED);
    }

    public static ReservationStatus confirmed() {
        return new ReservationStatus(Status.CONFIRMED);
    }

    public static ReservationStatus failed() {
        return new ReservationStatus(Status.FAILED);
    }

    public boolean isFinished() {
        return value.isFinished();
    }

    public boolean isConfirmed() {
        return value.isConfirmed();
    }
}
