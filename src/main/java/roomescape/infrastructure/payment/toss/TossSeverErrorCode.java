package roomescape.infrastructure.payment.toss;

/**
 * @see <a href="https://docs.tosspayments.com/reference/error-codes">토스 결제 오류 코드 정의서</a>
 */
public enum TossSeverErrorCode {
    INVALID_API_KEY,
    INVALID_AUTHORIZE_AUTH,
    UNAPPROVED_ORDER_ID,
    UNAUTHORIZED_KEY,
    INCORRECT_BASIC_AUTH_FORMAT
}
