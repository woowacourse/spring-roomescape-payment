package roomescape.payment.domain;

import java.util.Arrays;

public enum PaymentMethod {
    CARD("카드"),
    EASY_PAY("간편결제"),
    ;

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public static PaymentMethod from(String input) {
        return Arrays.stream(values())
                .filter(paymentMethod -> paymentMethod.method.equals(input))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("지원하지 않는 결제 방법입니다. (%s)", input)));
    }
}
