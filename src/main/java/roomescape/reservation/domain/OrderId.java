package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record OrderId(
        @Column(name = "order_id", nullable = false)
        String orderId
) {

}
