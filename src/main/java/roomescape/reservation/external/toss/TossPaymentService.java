package roomescape.reservation.external.toss;

public interface TossPaymentService {
    TossPaymentResponse paymentReservation(final TossPaymentRequest request);
}
