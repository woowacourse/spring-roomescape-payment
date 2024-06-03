package roomescape.payment.domain;

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
        for (PaymentType paymentType : PaymentType.values()) {
            if (paymentType.value.equals(value)) {
                return paymentType;
            }
        }
        throw new IllegalArgumentException(String.format("%s는 없는 값입니다.", value));
    }

    public String getValue() {
        return value;
    }
}
