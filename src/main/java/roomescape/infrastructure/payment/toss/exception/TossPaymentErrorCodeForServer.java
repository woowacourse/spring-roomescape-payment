package roomescape.infrastructure.payment.toss.exception;

import java.util.Arrays;
import java.util.Optional;

public enum TossPaymentErrorCodeForServer {
    INVALID_API_KEY("잘못된 시크릿키 연동 정보 입니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    NOT_FOUND_TERMINAL_ID("단말기번호(Terminal Id)가 없습니다. 토스페이먼츠로 문의 바랍니다."),
    INVALID_AUTHORIZE_AUTH("유효하지 않은 인증 방식입니다."),
    INVALID_UNREGISTERED_SUBMALL("등록되지 않은 서브몰입니다. 서브몰이 없는 가맹점이라면 안심클릭이나 ISP 결제가 필요합니다."),
    NOT_REGISTERED_BUSINESS("등록되지 않은 사업자 번호입니다."),
    UNAPPROVED_ORDER_ID("아직 승인되지 않은 주문번호입니다."),
    UNAUTHORIZED_KEY("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
    INCORRECT_BASIC_AUTH_FORMAT("잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."),
    FORBIDDEN_REQUEST("허용되지 않은 요청입니다."),
    NOT_FOUND_PAYMENT("존재하지 않는 결제 정보 입니다."),
    NOT_FOUND_PAYMENT_SESSION("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."),
    FAILED_INTERNAL_SYSTEM_PROCESSING("내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."),
    REJECT_CARD_COMPANY("결제 승인이 거절되었습니다.");

    private final String message;

    TossPaymentErrorCodeForServer(String message) {
        this.message = message;
    }

    public static Optional<TossPaymentErrorCodeForServer> of(String codeName) {
        return Arrays.stream(TossPaymentErrorCodeForServer.values())
            .filter(code -> code.name().equals(codeName))
            .findAny();
    }

    public String getMessage() {
        return message;
    }
}
