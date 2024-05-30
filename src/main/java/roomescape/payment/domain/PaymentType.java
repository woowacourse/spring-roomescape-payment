package roomescape.payment.domain;

import java.util.Arrays;

public enum PaymentType {
    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌"),
    EASY_PAYMENT("간편결제"),
    MOBILE_PHONE("휴대폰"),
    BANK_TRANSFER("계좌이체"),
    CULTURE_GIFT_CERTIFICATE("문화상품권"),
    BOOK_CULTURE_GIFT_CERTIFICATE("도서문화상품권"),
    GAME_CULTURE_GIFT_CERTIFICATE("게임문화상품권"),
    ;

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    public static PaymentType from(String value) {
        return Arrays.stream(PaymentType.values())
                .filter(paymentType -> paymentType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s의 결제 타입은 존재하지 않습니다.", value)));
    }

    public String getValue() {
        return value;
    }
}
