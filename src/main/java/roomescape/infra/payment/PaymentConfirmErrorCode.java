package roomescape.infra.payment;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.Arrays;
import org.springframework.http.HttpStatus;

public enum PaymentConfirmErrorCode {

    EXCEED_MAX_CARD_INSTALLMENT_PLAN(BAD_REQUEST, "설정 가능한 최대 할부 개월 수를 초과했습니다."),
    NOT_ALLOWED_POINT_USE(BAD_REQUEST, "포인트 사용이 불가한 카드로 카드 포인트 결제에 실패했습니다."),
    INVALID_REJECT_CARD(BAD_REQUEST, "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다."),
    BELOW_MINIMUM_AMOUNT(BAD_REQUEST, "신용카드는 결제금액이 100원 이상, 계좌는 200원이상부터 결제가 가능합니다."),
    INVALID_CARD_EXPIRATION(BAD_REQUEST, "유효하지 않은 카드입니다. 유효기간을 포함하여 카드 정보를 확인해주세요."),
    INVALID_STOPPED_CARD(BAD_REQUEST, "정지된 카드입니다."),
    EXCEED_MAX_DAILY_PAYMENT_COUNT(BAD_REQUEST, "하루 결제 가능 횟수를 초과했습니다."),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT(BAD_REQUEST, "할부가 지원되지 않는 카드 또는 가맹점입니다."),
    INVALID_CARD_INSTALLMENT_PLAN(BAD_REQUEST, "할부 개월 정보가 잘못되었습니다."),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN(BAD_REQUEST, "할부가 지원되지 않는 카드입니다."),
    EXCEED_MAX_PAYMENT_AMOUNT(BAD_REQUEST, "하루 결제 가능 금액을 초과했습니다."),
    INVALID_CARD_LOST_OR_STOLEN(BAD_REQUEST, "분실 혹은 도난 카드입니다."),
    RESTRICTED_TRANSFER_ACCOUNT(BAD_REQUEST, "계좌는 등록 후 12시간 뒤부터 결제할 수 있습니다. 관련 정책은 해당 은행으로 문의해주세요."),
    INVALID_CARD_NUMBER(BAD_REQUEST, "카드번호를 다시 확인해주세요."),
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT(BAD_REQUEST, "1일 출금 한도를 초과했습니다."),
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT(BAD_REQUEST, "1회 출금 한도를 초과했습니다."),
    CARD_PROCESSING_ERROR(BAD_REQUEST, "카드사에서 오류가 발생했습니다."),
    EXCEED_MAX_AMOUNT(BAD_REQUEST, "거래금액 한도를 초과했습니다."),
    INVALID_ACCOUNT_INFO_RE_REGISTER(BAD_REQUEST, "유효하지 않은 계좌입니다. 계좌 재등록 후 시도해주세요."),
    NOT_AVAILABLE_PAYMENT(BAD_REQUEST, "결제가 불가능한 시간대입니다."),
    REJECT_ACCOUNT_PAYMENT(BAD_REQUEST, "잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_PAYMENT(BAD_REQUEST, "한도초과 혹은 잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_COMPANY(BAD_REQUEST, "결제 승인이 거절되었습니다."),
    REJECT_TOSSPAY_INVALID_ACCOUNT(BAD_REQUEST, "선택하신 출금 계좌가 출금이체 등록이 되어 있지 않아요. 계좌를 다시 등록해 주세요."),
    EXCEED_MAX_AUTH_COUNT(BAD_REQUEST, "최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요."),
    EXCEED_MAX_ONE_DAY_AMOUNT(BAD_REQUEST, "일일 한도를 초과했습니다."),
    NOT_AVAILABLE_BANK(BAD_REQUEST, "은행 서비스 시간이 아닙니다."),
    INVALID_PASSWORD(BAD_REQUEST, "결제 비밀번호가 일치하지 않습니다."),
    FDS_ERROR(BAD_REQUEST, "[토스페이먼츠] 위험거래가 감지되어 결제가 제한됩니다. 발송된 문자에 포함된 링크를 통해 본인인증 후 결제가 가능합니다. (고객센터: 1644-8051)"),
    NOT_FOUND_PAYMENT(BAD_REQUEST, "존재하지 않는 결제 정보입니다."),
    NOT_FOUND_PAYMENT_SESSION(BAD_REQUEST, "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."),
    PAYMENT_CONFIRM_ERROR(INTERNAL_SERVER_ERROR, "결제 승인 중 오류가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    PaymentConfirmErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static PaymentConfirmErrorCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.isSameCode(code))
                .findFirst()
                .orElse(PAYMENT_CONFIRM_ERROR);
    }

    private boolean isSameCode(String code) {
        return this.name().equals(code);
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
