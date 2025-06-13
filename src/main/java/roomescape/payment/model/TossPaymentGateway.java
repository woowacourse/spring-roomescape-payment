package roomescape.payment.model;

public interface TossPaymentGateway {

    void requestApprove(final TossPaymentApprovalInfo tossPaymentApprovalInfo);

}
