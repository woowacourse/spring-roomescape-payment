package roomescape.payment.domain;

public interface PaymentClient {
    ConfirmedPayment confirm(NewPayment newPayment);
}
