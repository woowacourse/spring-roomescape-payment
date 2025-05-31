package roomescape.payment.toss.interceptor;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TossPaymentNotFoundErrorCode {

    NOT_FOUND_PAYMENT_SESSION("NOT_FOUND_PAYMENT_SESSION"),
    NOT_FOUND_PAYMENT("NOT_FOUND_PAYMENT"),
    ;

    private final String code;

    public static boolean isNotFoundError(String errorCode) {
        return Arrays.stream(values())
                .anyMatch(error -> error.code.equals(errorCode));
    }
}
