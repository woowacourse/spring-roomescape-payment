package roomescape.payment.controller.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import roomescape.global.response.ErrorCode;

@Getter
@RequiredArgsConstructor
public class PaymentErrorCode implements ErrorCode {

    private final String value;
    private final String message;
}
