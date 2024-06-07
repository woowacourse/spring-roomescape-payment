package roomescape.domain.payment;

import java.util.Arrays;
import roomescape.exception.payment.InvalidPaymentMethodException;

public enum PaymentMethod {
    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌"),
    SIMPLE_PAYMENT("간편결제"),
    MOBILE_PHONE("휴대폰"),
    BANK_TRANSFER("계좌이체"),
    CULTURE_VOUCHER("문화상품권"),
    BOOK_VOUCHER("도서문화상품권"),
    GAME_VOUCHER("게임문화상품권");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public static PaymentMethod findByDescription(String description) {
        return Arrays.stream(values())
                .filter(method -> method.description.equals(description))
                .findFirst()
                .orElseThrow(InvalidPaymentMethodException::new);
    }

    public String getDescription() {
        return description;
    }
}
