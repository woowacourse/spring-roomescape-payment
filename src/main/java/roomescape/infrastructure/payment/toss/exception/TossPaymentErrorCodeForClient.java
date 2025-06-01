package roomescape.infrastructure.payment.toss.exception;

import java.util.Arrays;
import java.util.Optional;

public enum TossPaymentErrorCodeForClient {
    ALREADY_PROCESSED_PAYMENT("이미 처리된 결제 입니다."),
    PROVIDER_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    EXCEED_MAX_CARD_INSTALLMENT_PLAN("설정 가능한 최대 할부 개월 수를 초과했습니다."),
    NOT_ALLOWED_POINT_USE("포인트 사용이 불가한 카드로 카드 포인트 결제에 실패했습니다."),
    INVALID_REJECT_CARD("카드 사용이 거절되었습니다. 카드사 문의가 필요합니다."),
    BELOW_MINIMUM_AMOUNT("신용카드는 결제금액이 100원 이상, 계좌는 200원이상부터 결제가 가능합니다."),
    INVALID_CARD_EXPIRATION("카드 정보를 다시 확인해주세요. (유효기간)"),
    INVALID_STOPPED_CARD("정지된 카드 입니다."),
    EXCEED_MAX_DAILY_PAYMENT_COUNT("하루 결제 가능 횟수를 초과했습니다."),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT("할부가 지원되지 않는 카드 또는 가맹점 입니다."),
    INVALID_CARD_INSTALLMENT_PLAN("할부 개월 정보가 잘못되었습니다."),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN("할부가 지원되지 않는 카드입니다."),
    EXCEED_MAX_PAYMENT_AMOUNT("하루 결제 가능 금액을 초과했습니다."),
    INVALID_CARD_LOST_OR_STOLEN("분실 혹은 도난 카드입니다."),
    RESTRICTED_TRANSFER_ACCOUNT("계좌는 등록 후 12시간 뒤부터 결제할 수 있습니다. 관련 정책은 해당 은행으로 문의해주세요."),
    INVALID_CARD_NUMBER("카드번호를 다시 확인해주세요."),
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT("1일 출금 한도를 초과했습니다."),
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT("1회 출금 한도를 초과했습니다."),
    CARD_PROCESSING_ERROR("카드사에서 오류가 발생했습니다."),
    EXCEED_MAX_AMOUNT("거래금액 한도를 초과했습니다."),
    INVALID_ACCOUNT_INFO_RE_REGISTER("유효하지 않은 계좌입니다. 계좌 재등록 후 시도해주세요."),
    NOT_AVAILABLE_PAYMENT("결제가 불가능한 시간대입니다"),
    EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT("당월 결제 가능금액인 1,000,000원을 초과 하셨습니다."),
    REJECT_ACCOUNT_PAYMENT("잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_PAYMENT("한도초과 혹은 잔액부족으로 결제에 실패했습니다."),
    REJECT_TOSSPAY_INVALID_ACCOUNT("선택하신 출금 계좌가 출금이체 등록이 되어 있지 않아요. 계좌를 다시 등록해 주세요."),
    EXCEED_MAX_AUTH_COUNT("최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요."),
    EXCEED_MAX_ONE_DAY_AMOUNT("일일 한도를 초과했습니다."),
    NOT_AVAILABLE_BANK("은행 서비스 시간이 아닙니다."),
    INVALID_PASSWORD("결제 비밀번호가 일치하지 않습니다."),
    FDS_ERROR(
        "[토스페이먼츠] 위험거래가 감지되어 결제가 제한됩니다. 발송된 문자에 포함된 링크를 통해 본인인증 후 결제가 가능합니다. (고객센터: 1644-8051)"),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING("결제가 완료되지 않았어요. 다시 시도해주세요."),
    UNKNOWN_PAYMENT_ERROR("결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요.");

    private final String message;

    TossPaymentErrorCodeForClient(String message) {
        this.message = message;
    }

    public static Optional<TossPaymentErrorCodeForClient> of(String codeName) {
        return Arrays.stream(TossPaymentErrorCodeForClient.values())
            .filter(code -> code.name().equals(codeName))
            .findAny();
    }

    public String getMessage() {
        return message;
    }
}
