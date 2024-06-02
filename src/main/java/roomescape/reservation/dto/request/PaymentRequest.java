package roomescape.reservation.dto.request;

public record PaymentRequest(
        int amount,
        String orderId,
        String paymentKey
) {

    public static PaymentRequest toRequest(ReservationCreateRequest reservationCreateRequest){
        return new PaymentRequest(reservationCreateRequest.amount(), reservationCreateRequest.orderId(),
                reservationCreateRequest.paymentKey());
    }
}
