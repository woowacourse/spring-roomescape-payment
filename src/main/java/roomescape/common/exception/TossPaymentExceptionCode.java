package roomescape.common.exception;

import java.util.Arrays;
import org.springframework.http.HttpStatus;

public enum TossPaymentExceptionCode {
    ALREADY_PROCESSED_PAYMENT(org.springframework.http.HttpStatus.BAD_REQUEST, "이미 처리된 결제 입니다."),
    EXCEED_MAX_CARD_INSTALLMENT_PLAN(HttpStatus.BAD_REQUEST, "설정 가능한 최대 할부 개월 수를 초과했습니다."),
    NOT_ALLOWED_POINT_USE(HttpStatus.BAD_REQUEST, "포인트 사용이 불가한 카드로 카드 포인트 결제에 실패했습니다."),
    INVALID_REJECT_CARD(HttpStatus.BAD_REQUEST, "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다."),
    BELOW_MINIMUM_AMOUNT(HttpStatus.BAD_REQUEST, "신용카드는 결제금액이 100원 이상, 계좌는 200원이상부터 결제가 가능합니다."),
    INVALID_CARD_EXPIRATION(HttpStatus.BAD_REQUEST, "카드 정보를 다시 확인해주세요."),
    INVALID_STOPPED_CARD(HttpStatus.BAD_REQUEST, "정지된 카드 입니다."),
    EXCEED_MAX_DAILY_PAYMENT_COUNT(HttpStatus.BAD_REQUEST, "하루 결제 가능 횟수를 초과했습니다."),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT(HttpStatus.BAD_REQUEST, "할부가 지원되지 않는 카드 또는 가맹점 입니다."),
    INVALID_CARD_INSTALLMENT_PLAN(HttpStatus.BAD_REQUEST, "할부 개월 정보가 잘못되었습니다."),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN(HttpStatus.BAD_REQUEST, "할부가 지원되지 않는 카드입니다."),
    EXCEED_MAX_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "하루 결제 가능 금액을 초과했습니다."),
    INVALID_CARD_LOST_OR_STOLEN(HttpStatus.BAD_REQUEST, "분실 혹은 도난 카드입니다."),
    RESTRICTED_TRANSFER_ACCOUNT(HttpStatus.BAD_REQUEST, "계좌는 등록 후 12시간 뒤부터 결제할 수 있습니다. 관련 정책은 해당 은행으로 문의해주세요."),
    INVALID_CARD_NUMBER(HttpStatus.BAD_REQUEST, "카드번호를 다시 확인해주세요."),
    INVALID_UNREGISTERED_SUBMALL(HttpStatus.BAD_REQUEST, "등록되지 않은 서브몰입니다. 서브몰이 없는 가맹점이라면 안심클릭이나 ISP 결제가 필요합니다."),
    NOT_REGISTERED_BUSINESS(HttpStatus.BAD_REQUEST, "등록되지 않은 사업자 번호입니다."),
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT(HttpStatus.BAD_REQUEST, "1일 출금 한도를 초과했습니다."),
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT(HttpStatus.BAD_REQUEST, "1회 출금 한도를 초과했습니다."),
    EXCEED_MAX_AMOUNT(HttpStatus.BAD_REQUEST, "거래금액 한도를 초과했습니다."),
    INVALID_ACCOUNT_INFO_RE_REGISTER(HttpStatus.BAD_REQUEST, "유효하지 않은 계좌입니다. 계좌 재등록 후 시도해주세요."),
    NOT_AVAILABLE_PAYMENT(HttpStatus.BAD_REQUEST, "결제가 불가능한 시간대입니다"),
    REJECT_ACCOUNT_PAYMENT(HttpStatus.BAD_REQUEST, "잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_PAYMENT(HttpStatus.BAD_REQUEST, "한도초과 혹은 잔액부족으로 결제에 실패했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    TossPaymentExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static TossPaymentExceptionCode from(String code) {
        return Arrays.stream(values())
                .filter(tossPaymentExceptionCode -> tossPaymentExceptionCode.name().equals(code))
                .findFirst()
                .orElse(INTERNAL_SERVER_ERROR);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
