package roomescape.infrastructure.payment.toss;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

public enum TossPaymentErrorCode {
    // 결제 승인
    ALREADY_PROCESSED_PAYMENT(400, "이미 처리된 결제 입니다."),
    EXCEED_MAX_CARD_INSTALLMENT_PLAN(400, "설정 가능한 최대 할부 개월 수를 초과했습니다."),
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    NOT_ALLOWED_POINT_USE(400, "포인트 사용이 불가한 카드로 카드 포인트 결제에 실패했습니다."),
    INVALID_REJECT_CARD(400, "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다."),
    BELOW_MINIMUM_AMOUNT(400, "신용카드는 결제금액이 100원 이상, 계좌는 200원이상부터 결제가 가능합니다."),
    INVALID_CARD_EXPIRATION(400, "카드 정보를 다시 확인해주세요. (유효기간)"),
    INVALID_STOPPED_CARD(400, "정지된 카드 입니다."),
    EXCEED_MAX_DAILY_PAYMENT_COUNT(400, "하루 결제 가능 횟수를 초과했습니다."),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT(400, "할부가 지원되지 않는 카드 또는 가맹점 입니다."),
    INVALID_CARD_INSTALLMENT_PLAN(400, "할부 개월 정보가 잘못되었습니다."),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN(400, "할부가 지원되지 않는 카드입니다."),
    EXCEED_MAX_PAYMENT_AMOUNT(400, "하루 결제 가능 금액을 초과했습니다."),
    NOT_FOUND_TERMINAL_ID(400, "단말기번호(Terminal Id)가 없습니다. 토스페이먼츠로 문의 바랍니다."),
    INVALID_CARD_LOST_OR_STOLEN(400, "분실 혹은 도난 카드입니다."),
    RESTRICTED_TRANSFER_ACCOUNT(400, "계좌는 등록 후 12시간 뒤부터 결제할 수 있습니다. 관련 정책은 해당 은행으로 문의해주세요."),
    INVALID_CARD_NUMBER(400, "카드번호를 다시 확인해주세요."),
    INVALID_UNREGISTERED_SUBMALL(400, "등록되지 않은 서브몰입니다. 서브몰이 없는 가맹점이라면 안심클릭이나 ISP 결제가 필요합니다."),
    NOT_REGISTERED_BUSINESS(400, "등록되지 않은 사업자 번호입니다. 매장에 문의하세요"),
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT(400, "1일 출금 한도를 초과했습니다."),
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT(400, "1회 출금 한도를 초과했습니다."),
    CARD_PROCESSING_ERROR(400, "카드사에서 오류가 발생했습니다."),
    EXCEED_MAX_AMOUNT(400, "거래금액 한도를 초과했습니다."),
    INVALID_ACCOUNT_INFO_RE_REGISTER(400, "유효하지 않은 계좌입니다. 계좌 재등록 후 시도해주세요."),
    NOT_AVAILABLE_PAYMENT(400, "결제가 불가능한 시간대입니다."),
    UNAPPROVED_ORDER_ID(400, "아직 승인되지 않은 주문번호입니다."),
    REJECT_ACCOUNT_PAYMENT(403, "잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_PAYMENT(403, "한도초과 혹은 잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_COMPANY(403, "결제 승인이 거절되었습니다."),
    FORBIDDEN_REQUEST(403, "허용되지 않은 요청입니다."),
    REJECT_TOSSPAY_INVALID_ACCOUNT(403, "선택하신 출금 계좌가 출금이체 등록이 되어 있지 않아요. 계좌를 다시 등록해 주세요."),
    EXCEED_MAX_AUTH_COUNT(403, "최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요."),
    EXCEED_MAX_ONE_DAY_AMOUNT(403, "일일 한도를 초과했습니다."),
    NOT_AVAILABLE_BANK(403, "은행 서비스 시간이 아닙니다."),
    INVALID_PASSWORD(403, "결제 비밀번호가 일치하지 않습니다."),
    FDS_ERROR(403, "[토스페이먼츠] 위험거래가 감지되어 결제가 제한됩니다. 발송된 문자에 포함된 링크를 통해 본인인증 후 결제가 가능합니다. (고객센터: 1644-8051)"),
    NOT_FOUND_PAYMENT(404, "존재하지 않는 결제 정보 입니다."),
    NOT_FOUND_PAYMENT_SESSION(404, "결제 시간이 만료되었습니다."),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(500, "결제가 완료되지 않았어요. 다시 시도해주세요."),
    FAILED_INTERNAL_SYSTEM_PROCESSING(500, "결제에 실패했습니다. 잠시 후 다시 시도해주세요."),
    UNKNOWN_PAYMENT_ERROR(500, "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요."),

    // 결제 취소
    ALREADY_CANCELED_PAYMENT(400, "이미 취소된 결제 입니다."),
    INVALID_REFUND_ACCOUNT_INFO(400, "환불 계좌번호와 예금주명이 일치하지 않습니다."),
    EXCEED_CANCEL_AMOUNT_DISCOUNT_AMOUNT(400, "즉시할인금액보다 적은 금액은 부분취소가 불가능합니다."),
    INVALID_REFUND_ACCOUNT_NUMBER(400, "잘못된 환불 계좌번호입니다."),
    INVALID_BANK(400, "유효하지 않은 은행입니다."),
    NOT_MATCHES_REFUNDABLE_AMOUNT(400, "잔액 결과가 일치하지 않습니다."),
    PROVIDER_ERROR(400, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    REFUND_REJECTED(400, "환불이 거절됐습니다. 결제사에 문의 부탁드립니다."),
    ALREADY_REFUND_PAYMENT(400, "이미 환불된 결제입니다."),
    NOT_CANCELABLE_AMOUNT(403, "취소 할 수 없는 금액 입니다."),
    FORBIDDEN_CONSECUTIVE_REQUEST(403, "반복적인 요청은 허용되지 않습니다. 잠시 후 다시 시도해주세요."),
    NOT_CANCELABLE_PAYMENT(403, "취소 할 수 없는 결제 입니다."),
    EXCEED_MAX_REFUND_DUE(403, "환불 가능한 기간이 지났습니다."),
    NOT_ALLOWED_PARTIAL_REFUND_WAITING_DEPOSIT(403, "입금 대기중인 결제는 부분 환불이 불가합니다."),
    NOT_ALLOWED_PARTIAL_REFUND(403,
            "에스크로 주문, 현금 카드 결제일 때는 부분 환불이 불가합니다. 이외 다른 결제 수단에서 부분 취소가 되지 않을 때는 토스페이먼츠에 문의해 주세요."),
    NOT_CANCELABLE_PAYMENT_FOR_DORMANT_USER(403, "휴면 처리된 회원의 결제는 취소할 수 없습니다."),
    FAILED_REFUND_PROCESS(500, "은행 응답시간 지연이나 일시적인 오류로 환불요청에 실패했습니다."),
    FAILED_METHOD_HANDLING_CANCEL(500, "취소 중 결제 시 사용한 결제 수단 처리과정에서 일시적인 오류가 발생했습니다."),
    FAILED_PARTIAL_REFUND(500, "은행 점검, 해약 계좌 등의 사유로 부분 환불이 실패했습니다."),

    SERVER_ERROR(500, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

    private static final Map<String, TossPaymentErrorCode> SUIT_MESSAGE = Arrays.stream(values())
            .collect(Collectors.toMap(TossPaymentErrorCode::name, Function.identity()));

    private final int statusCode;
    private final String message;

    TossPaymentErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static TossPaymentErrorCode find(String errorCode) {
        if (SUIT_MESSAGE.containsKey(errorCode)) {
            return SUIT_MESSAGE.get(errorCode);
        }
        return SERVER_ERROR;
    }

    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(statusCode);
    }

    public String getMessage() {
        return message;
    }
}
