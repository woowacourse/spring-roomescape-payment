package roomescape.dto;

public record PaymentRequest(String paymentKey, String orderId, long amount) {
    public static PaymentRequest from(ReservationPaymentRequest request){
        return new PaymentRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
