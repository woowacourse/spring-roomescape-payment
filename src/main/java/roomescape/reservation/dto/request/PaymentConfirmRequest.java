package roomescape.reservation.dto.request;

public record PaymentConfirmRequest(String paymentKey, String orderId, Long amount) {

    public static PaymentConfirmRequest from(ReservationDetailRequest detailRequest) {
        return new PaymentConfirmRequest(detailRequest.paymentKey(), detailRequest.orderId(), detailRequest.amount());
    }
}
