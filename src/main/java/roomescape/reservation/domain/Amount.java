package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Amount(
        @Column(nullable = false)
        Long amount
) {

}
