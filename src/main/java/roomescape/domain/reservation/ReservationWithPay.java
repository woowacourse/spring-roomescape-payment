package roomescape.domain.reservation;

public record ReservationWithPay(
        Reservation reservation,
        Payment payment
) {
}
