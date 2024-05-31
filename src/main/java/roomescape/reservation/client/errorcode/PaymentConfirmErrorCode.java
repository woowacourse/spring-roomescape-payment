package roomescape.reservation.client.errorcode;

import java.util.Arrays;
import java.util.Optional;

public enum PaymentConfirmErrorCode {
    INVALID_API_KEY,
    UNAUTHORIZED_KEY,
    INCORRECT_BASIC_AUTH_FORMAT,
    DEFAULT_ERROR_CODE;

    public static Optional<PaymentConfirmErrorCode> findByErrorCode(String errorCode) {
        return Arrays.stream(PaymentConfirmErrorCode.values())
                .filter(element -> element.name().equals(errorCode))
                .findFirst();
    }
}
