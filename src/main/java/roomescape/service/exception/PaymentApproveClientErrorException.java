package roomescape.service.exception;

public class PaymentApproveClientErrorException extends RuntimeException {

    public PaymentApproveClientErrorException() {
        super("결제 승인 API 호출 도중 문제가 발생했습니다.");
    }
}
