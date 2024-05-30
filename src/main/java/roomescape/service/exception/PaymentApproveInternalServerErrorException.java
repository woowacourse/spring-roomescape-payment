package roomescape.service.exception;

public class PaymentApproveInternalServerErrorException extends RuntimeException {

    public PaymentApproveInternalServerErrorException() {
        super("결제 승인 API 서버에서 문제가 발생했습니다.");
    }
}
