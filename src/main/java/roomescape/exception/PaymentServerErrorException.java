package roomescape.exception;

public class PaymentServerErrorException extends PaymentException {

    public PaymentServerErrorException() {
        super("결제 시스템이 원활하게 동작하지 않습니다.");
    }
}
