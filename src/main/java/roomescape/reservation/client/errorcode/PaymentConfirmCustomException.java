package roomescape.reservation.client.errorcode;

public class PaymentConfirmCustomException extends RuntimeException {
    private final PaymentConfirmErrorCode errorCode;
    private final String message;

    public PaymentConfirmCustomException(PaymentConfirmErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
