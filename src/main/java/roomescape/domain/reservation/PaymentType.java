package roomescape.domain.reservation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentType {

    TOSS("토스"),
    ;

    private final String description;
}
