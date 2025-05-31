package roomescape.infrastructure.payment.toss;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.Arrays;
import org.springframework.http.HttpStatus;
import roomescape.exception.code.ErrorCode;

/**
 * @see <a href="https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8"> 토스 결제 승인
 * API 에러 코드 문서</a>
 */
public enum TossPaymentErrorCode implements ErrorCode {

    EXCEED_MAX_DAILY_PAYMENT_COUNT(
            BAD_REQUEST, "하루 결제 가능 횟수를 초과했습니다."),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT(
            BAD_REQUEST, "할부가 지원되지 않는 카드 또는 가맹점 입니다."),
    INVALID_CARD_INSTALLMENT_PLAN(
            BAD_REQUEST, "할부 개월 정보가 잘못되었습니다."),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN(
            BAD_REQUEST, "할부가 지원되지 않는 카드입니다."),
    EXCEED_MAX_PAYMENT_AMOUNT(
            BAD_REQUEST, "하루 결제 가능 금액을 초과했습니다."),
    NOT_FOUND_TERMINAL_ID(
            BAD_REQUEST, "단말기번호(Terminal Id)가 없습니다. 토스페이먼츠로 문의 바랍니다."),
    INVALID_AUTHORIZE_AUTH(
            BAD_REQUEST, "유효하지 않은 인증 방식입니다."),
    INVALID_CARD_LOST_OR_STOLEN(
            BAD_REQUEST, "분실 혹은 도난 카드입니다."),
    RESTRICTED_TRANSFER_ACCOUNT(
            BAD_REQUEST, "계좌는 등록 후 12시간 뒤부터 결제할 수 있습니다."),
    INVALID_CARD_NUMBER(
            BAD_REQUEST, "카드번호를 다시 확인해주세요."),
    INVALID_UNREGISTERED_SUBMALL(
            BAD_REQUEST, "등록되지 않은 서브몰입니다. 서브몰이 없는 가맹점이라면 안심클릭이나 ISP 결제가 필요합니다."),
    NOT_REGISTERED_BUSINESS(
            BAD_REQUEST, "등록되지 않은 사업자 번호입니다."),
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT(
            BAD_REQUEST, "1일 출금 한도를 초과했습니다."),
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT(
            BAD_REQUEST, "1회 출금 한도를 초과했습니다."),
    CARD_PROCESSING_ERROR(
            BAD_REQUEST, "카드사에서 오류가 발생했습니다."),
    EXCEED_MAX_AMOUNT(
            BAD_REQUEST, "거래금액 한도를 초과했습니다."),
    INVALID_ACCOUNT_INFO_RE_REGISTER(
            BAD_REQUEST, "유효하지 않은 계좌입니다. 계좌 재등록 후 시도해주세요."),
    NOT_AVAILABLE_PAYMENT(
            BAD_REQUEST, "결제가 불가능한 시간대입니다."),
    UNAPPROVED_ORDER_ID(
            BAD_REQUEST, "아직 승인되지 않은 주문번호입니다."),
    EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT(
            BAD_REQUEST, "당월 결제 가능금액인 1,000,000원을 초과 하셨습니다."),

    UNAUTHORIZED_KEY(
            UNAUTHORIZED, "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),

    REJECT_ACCOUNT_PAYMENT(
            FORBIDDEN, "잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_PAYMENT(
            FORBIDDEN, "한도초과 혹은 잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_COMPANY(
            FORBIDDEN, "결제 승인이 거절되었습니다."),
    FORBIDDEN_REQUEST(
            FORBIDDEN, "허용되지 않은 요청입니다."),
    REJECT_TOSSPAY_INVALID_ACCOUNT(
            FORBIDDEN, "선택하신 출금 계좌가 출금이체 등록이 되어 있지 않아요. 계좌를 다시 등록해 주세요."),
    EXCEED_MAX_AUTH_COUNT(
            FORBIDDEN, "최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요."),
    EXCEED_MAX_ONE_DAY_AMOUNT(
            FORBIDDEN, "일일 한도를 초과했습니다."),
    NOT_AVAILABLE_BANK(
            FORBIDDEN, "은행 서비스 시간이 아닙니다."),
    INVALID_PASSWORD(
            FORBIDDEN, "결제 비밀번호가 일치하지 않습니다."),
    INCORRECT_BASIC_AUTH_FORMAT(
            FORBIDDEN, "잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."),
    FDS_ERROR(
            FORBIDDEN, "[토스페이먼츠] 위험거래가 감지되어 결제가 제한됩니다. 발송된 문자에 포함된 링크를 통해 본인인증 후 결제가 가능합니다."),

    NOT_FOUND_PAYMENT(NOT_FOUND, "존재하지 않는 결제 정보 입니다."),
    NOT_FOUND_PAYMENT_SESSION(NOT_FOUND, "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."),

    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(
            INTERNAL_SERVER_ERROR, "결제가 완료되지 않았어요. 다시 시도해주세요."),
    FAILED_INTERNAL_SYSTEM_PROCESSING(
            INTERNAL_SERVER_ERROR, "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."),
    UNKNOWN_PAYMENT_ERROR(
            INTERNAL_SERVER_ERROR, "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요."),

    UNKNOWN(
            INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");

    private final HttpStatus statusCode;
    private final String message;
    private static final String MESSAGE_PREFIX = "[TOSS] ";

    TossPaymentErrorCode(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static TossPaymentErrorCode from(String code) {
        return Arrays.stream(values()).filter(e -> e.name().equals(code)).findFirst().orElse(UNKNOWN);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return MESSAGE_PREFIX + message;
    }
}
