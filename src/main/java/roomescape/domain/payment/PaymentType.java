package roomescape.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentType {

    TOSS("토스"),
    ADMIN("관리자")
    ;

    private final String description;
}
