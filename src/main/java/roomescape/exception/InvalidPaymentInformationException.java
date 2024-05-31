package roomescape.exception;

public class InvalidPaymentInformationException extends PaymentException {

    public InvalidPaymentInformationException() {
        super("결제 정보가 일치하지 않습니다.");
    }
}
