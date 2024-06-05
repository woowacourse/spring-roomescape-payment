package roomescape.exception;

import java.util.Arrays;
import org.springframework.http.HttpStatus;

public enum PaymentApproveErrorCode {
    ALREADY_PROCESSED_PAYMENT("이미 결제되었습니다."),
    PROVIDER_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    INVALID_REQUEST("잘못된 요청입니다."),
    EXCEED_MAX_CARD_INSTALLMENT_PLAN(INVALID_REQUEST.message),
    NOT_ALLOWED_POINT_USE("포인트 사용이 불가한 카드로 카드 포인트 결제에 실패했습니다."),
    INVALID_API_KEY(INVALID_REQUEST.message),
    INVALID_REJECT_CARD("카드 사용이 거절되었습니다. 카드사에 문의해보세요."),
    BELOW_MINIMUM_AMOUNT("신용카드는 결제금액이 100원 이상, 계좌는 200원이상부터 결제가 가능합니다."),
    INVALID_CARD_EXPIRATION("카드 유효기간을 다시 확인해 주세요."),
    INVALID_STOPPED_CARD("정지된 카드입니다."),
    EXCEED_MAX_DAILY_PAYMENT_COUNT("하루 결제 가능 횟수를 초과했습니다."),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT("할부가 지원되지 않는 카드 또는 가맹점입니다."),
    INVALID_CARD_INSTALLMENT_PLAN(INVALID_REQUEST.message),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN("할부가 지원되지 않는 카드입니다."),
    EXCEED_MAX_PAYMENT_AMOUNT("하루 결제 가능 금액을 초과했습니다."),
    NOT_FOUND_TERMINAL_ID(INVALID_REQUEST.message),
    INVALID_AUTHORIZE_AUTH(INVALID_REQUEST.message),
    INVALID_CARD_LOST_OR_STOLEN("분실 혹은 도난 카드입니다. 다른 카드를 사용해 주세요."),
    RESTRICTED_TRANSFER_ACCOUNT("계좌 결제는 계좌 등록 12시간 뒤부터 가능합니다."),
    INVALID_CARD_NUMBER("카드 번호를 다시 확인해 주세요."),
    INVALID_UNREGISTERED_SUBMALL("등록되지 않은 서브몰입니다."),
    NOT_REGISTERED_BUSINESS("등록되지 않은 사업자 번호입니다."),
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT("1일 출금 한도를 초과했습니다."),
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT("1회 출금 한도를 초과했습니다."),
    CARD_PROCESSING_ERROR(INVALID_REQUEST.message),
    EXCEED_MAX_AMOUNT("거래금액 한도를 초과했습니다."),
    INVALID_ACCOUNT_INFO_RE_REGISTER("유효하지 않은 계좌입니다. 계좌 재등록 후 다시 시도해주세요."),
    NOT_AVAILABLE_PAYMENT("현재 결제가 불가능합니다."),
    UNAPPROVED_ORDER_ID(INVALID_REQUEST.message),
    UNAUTHORIZED_KEY(INVALID_REQUEST.message),
    REJECT_ACCOUNT_PAYMENT("잔액이 부족합니다."),
    REJECT_CARD_PAYMENT("한도초과 혹은 잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_COMPANY("결제 승인이 거절되었습니다."),
    FORBIDDEN_REQUEST(INVALID_REQUEST.message),
    REJECT_TOSSPAY_INVALID_ACCOUNT("선택하신 출금 계좌가 출금이체 등록이 되어 있지 않습니다. 계좌를 다시 등록해 주세요."),
    EXCEED_MAX_AUTH_COUNT("최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요."),
    EXCEED_MAX_ONE_DAY_AMOUNT("일일 한도를 초과했습니다."),
    NOT_AVAILABLE_BANK("현재 은행 점검 시간입니다."),
    INVALID_PASSWORD("결제 비밀번호가 일치하지 않습니다."),
    INCORRECT_BASIC_AUTH_FORMAT(INVALID_REQUEST.message),
    FDS_ERROR("위험거래가 감지되어 결제가 제한됩니다. 발송된 문자에 포함된 링크를 통해 본인인증 후 진행해주세요."),
    NOT_FOUND_PAYMENT(INVALID_REQUEST.message),
    NOT_FOUND_PAYMENT_SESSION("결제 시간이 만료되었습니다. 다시 시도해주세요."),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(INVALID_REQUEST.message),
    FAILED_INTERNAL_SYSTEM_PROCESSING(INVALID_REQUEST.message),
    UNKNOWN_PAYMENT_ERROR("결제에 실패했습니다. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요.");

    private final String message;

    PaymentApproveErrorCode(final String message) {
        this.message = message;
    }

    public static String getMessageOf(final String code) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.name().equals(code))
                .findAny()
                .map(errorCode -> errorCode.message)
                .orElseThrow(() -> new PaymentException("알 수 없는 오류가 발생했습니다.",
                        HttpStatus.BAD_GATEWAY));
    }
}
