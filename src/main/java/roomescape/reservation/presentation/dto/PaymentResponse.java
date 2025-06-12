package roomescape.reservation.presentation.dto;

public class PaymentResponse {
    private String paymentKey;
    private Integer amount;

    private PaymentResponse() {
    }

    public PaymentResponse(final String paymentKey, final Integer amount) {
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Integer getAmount() {
        return amount;
    }
}
