package roomescape.domain.reservation;

import java.util.List;

public enum Status {
    WAITING,
    RESERVED,
    CANCELED,
    PAYMENT_PENDING;

    public static List<Status> getStatusWithoutCancel() {
        return List.of(RESERVED, PAYMENT_PENDING, WAITING);
    }
}
