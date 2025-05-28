package roomescape.reservation.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentClientProperties {

    private final String secretKey;
    private final Long expireLength;
}
