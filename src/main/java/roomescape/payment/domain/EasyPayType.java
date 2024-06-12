package roomescape.payment.domain;

import static roomescape.exception.type.UserPaymentExceptionType.UNSUPPORTED_PAY_TYPE;

import java.util.Arrays;

import roomescape.exception.PaymentException;
import roomescape.exception.response.UserPaymentExceptionResponse;

public enum EasyPayType {
    TOSS_PAY("토스페이"),
    KAKAO_PAY("카카오페이"),
    NAVER_PAY("네이버 페이"),
    SAMSUNG_PAY("삼성페이"),
    APPLE_PAY("애플페이"),
    L_PAY("엘페이"),
    PIN_PAY("핀페이"),
    PAYCO("페이코"),
    SSG_PAY("SSG페이");

    private String title;

    EasyPayType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static EasyPayType from(String easyPay) {
        return Arrays.stream(values())
                .filter(easyPayType -> easyPayType.title.equals(easyPay))
                .findAny()
                .orElseThrow(() -> new PaymentException(UserPaymentExceptionResponse.from(UNSUPPORTED_PAY_TYPE)));
    }
}
