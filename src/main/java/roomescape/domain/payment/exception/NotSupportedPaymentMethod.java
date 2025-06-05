package roomescape.domain.payment.exception;

public class NotSupportedPaymentMethod extends RuntimeException {
    public NotSupportedPaymentMethod() {
        super("지원하지 않는 결제 수단입니다.");
    }
}
