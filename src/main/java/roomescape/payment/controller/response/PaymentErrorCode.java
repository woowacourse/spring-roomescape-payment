package roomescape.payment.controller.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import roomescape.global.response.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    NOT_FOUND_PAYMENT_SESSION("PMT001", "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."),
    REJECT_CARD_COMPANY("PMT002", "카드사에서 결제를 거절하였습니다. 카드 정보를 확인해주세요."),
    FORBIDDEN_REQUEST("PMT003", "결제 요청이 금지된 상태입니다. 관리자에게 문의해주세요."),
    UNAUTHORIZED_KEY("PMT004", "인증되지 않은 키입니다. 관리자에게 문의해주세요."),
    ;

    private final String value;
    private final String message;
}
