package roomescape.exception;

public class PaymentClientException extends PaymentException {

    public PaymentClientException() {
        super("결제 정보가 일치하지 않습니다.");
    }
}
