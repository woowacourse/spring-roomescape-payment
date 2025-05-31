package roomescape.application.payment;

import java.util.EnumSet;
import java.util.Set;
import roomescape.infrastructure.error.exception.PaymentException;

public enum TossPaymentErrorCode {

    INVALID_ERROR_CODE("알 수 없는 결제 오류 코드입니다."),
    ALREADY_PROCESSED_PAYMENT("이미 처리된 결제 입니다."),
    PROVIDER_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    EXCEED_MAX_CARD_INSTALLMENT_PLAN("설정 가능한 최대 할부 개월 수를 초과했습니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    NOT_ALLOWED_POINT_USE("포인트 사용이 불가한 카드로 카드 포인트 결제에 실패했습니다."),
    INVALID_API_KEY("잘못된 시크릿키 연동 정보 입니다."),
    INVALID_REJECT_CARD("카드 사용이 거절되었습니다. 카드사 문의가 필요합니다."),
    BELOW_MINIMUM_AMOUNT("신용카드는 결제금액이 100원 이상, 계좌는 200원이상부터 결제가 가능합니다."),
    INVALID_CARD_EXPIRATION("카드 정보를 다시 확인해주세요. (유효기간)"),
    INVALID_STOPPED_CARD("정지된 카드 입니다."),
    EXCEED_MAX_DAILY_PAYMENT_COUNT("하루 결제 가능 횟수를 초과했습니다."),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT("할부가 지원되지 않는 카드 또는 가맹점 입니다."),
    INVALID_CARD_INSTALLMENT_PLAN("할부 개월 정보가 잘못되었습니다."),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN("할부가 지원되지 않는 카드입니다."),
    EXCEED_MAX_PAYMENT_AMOUNT("하루 결제 가능 금액을 초과했습니다."),
    NOT_FOUND_TERMINAL_ID("단말기번호(Terminal Id)가 없습니다. 토스페이먼츠로 문의 바랍니다."),
    INVALID_AUTHORIZE_AUTH("유효하지 않은 인증 방식입니다."),
    INVALID_CARD_LOST_OR_STOLEN("분실 혹은 도난 카드입니다."),
    RESTRICTED_TRANSFER_ACCOUNT("계좌는 등록 후 12시간 뒤부터 결제할 수 있습니다. 관련 정책은 해당 은행으로 문의해주세요."),
    INVALID_CARD_NUMBER("카드번호를 다시 확인해주세요."),
    INVALID_UNREGISTERED_SUBMALL("등록되지 않은 서브몰입니다. 서브몰이 없는 가맹점이라면 안심클릭이나 ISP 결제가 필요합니다."),
    NOT_REGISTERED_BUSINESS("등록되지 않은 사업자 번호입니다."),
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT("1일 출금 한도를 초과했습니다."),
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT("1회 출금 한도를 초과했습니다."),
    CARD_PROCESSING_ERROR("카드사에서 오류가 발생했습니다."),
    EXCEED_MAX_AMOUNT("거래금액 한도를 초과했습니다."),
    INVALID_ACCOUNT_INFO_RE_REGISTER("유효하지 않은 계좌입니다. 계좌 재등록 후 시도해주세요."),
    NOT_AVAILABLE_PAYMENT("결제가 불가능한 시간대입니다"),
    UNAPPROVED_ORDER_ID("아직 승인되지 않은 주문번호입니다."),
    EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT("당월 결제 가능금액인 1,000,000원을 초과 하셨습니다."),
    UNAUTHORIZED_KEY("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
    REJECT_ACCOUNT_PAYMENT("잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_PAYMENT("한도초과 혹은 잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_COMPANY("결제 승인이 거절되었습니다."),
    FORBIDDEN_REQUEST("허용되지 않은 요청입니다."),
    REJECT_TOSSPAY_INVALID_ACCOUNT("선택하신 출금 계좌가 출금이체 등록이 되어 있지 않아요. 계좌를 다시 등록해 주세요."),
    EXCEED_MAX_AUTH_COUNT("최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요."),
    EXCEED_MAX_ONE_DAY_AMOUNT("일일 한도를 초과했습니다."),
    NOT_AVAILABLE_BANK("은행 서비스 시간이 아닙니다."),
    INVALID_PASSWORD("결제 비밀번호가 일치하지 않습니다."),
    INCORRECT_BASIC_AUTH_FORMAT("잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."),
    FDS_ERROR("[토스페이먼츠] 위험거래가 감지되어 결제가 제한됩니다. 발송된 문자에 포함된 링크를 통해 본인인증 후 결제가 가능합니다. (고객센터: 1644-8051)"),
    NOT_FOUND_PAYMENT("존재하지 않는 결제 정보 입니다."),
    NOT_FOUND_PAYMENT_SESSION("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."),
    ;

    public static final String SYSTEM_ERROR_MESSAGE = "토스 결제 시스템에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
    private static final Set<TossPaymentErrorCode> SENSITIVE_ERROR_CODES = EnumSet.of(
            INVALID_API_KEY,
            NOT_FOUND_TERMINAL_ID,
            INVALID_UNREGISTERED_SUBMALL,
            NOT_REGISTERED_BUSINESS,
            UNAUTHORIZED_KEY,
            INCORRECT_BASIC_AUTH_FORMAT,
            NOT_FOUND_PAYMENT,
            NOT_FOUND_PAYMENT_SESSION
    );

    private final String koreanMessage;

    TossPaymentErrorCode(String koreanMessage) {
        this.koreanMessage = koreanMessage;
    }

    public static TossPaymentErrorCode fromCode(String code) {
        for (TossPaymentErrorCode errorCode : values()) {
            if (errorCode.name().equalsIgnoreCase(code)) {
                return errorCode;
            }
        }
        throw new PaymentException("알 수 없는 결제 오류 코드입니다: " + code);
    }

    public String getKoreanMessage() {
        if (isSensitiveErrorMessage()) {
            return "결제 승인 요청에 실패했습니다. 관리자에게 문의해주세요.";
        }
        return koreanMessage;
    }

    private boolean isSensitiveErrorMessage() {
        return SENSITIVE_ERROR_CODES.contains(this);
    }
}
