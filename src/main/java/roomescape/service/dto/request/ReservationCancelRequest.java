package roomescape.service.dto.request;

public record ReservationCancelRequest(long id, String cancelReason) {

    public PaymentCancelRequest toPaymentCancelRequest(String paymentKey) {
        return new PaymentCancelRequest(paymentKey, cancelReason);
    }
}
