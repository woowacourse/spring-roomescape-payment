package roomescape.reservation.dto.request;

public record ReservationCreateRequest(ReservationRequest reservation, PaymentRequest payment) {
}
