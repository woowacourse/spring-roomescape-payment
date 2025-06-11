package roomescape.domain.reservation;

public record ReservationWithPayment(
        Reservation reservation,
        String paymentKey,
        Long amount) {
}
