package roomescape.model;

public class ReservationWithPaymentInfo {

    private Reservation reservation;
    private PaymentInfo paymentInfo;

    public ReservationWithPaymentInfo(Reservation reservation, PaymentInfo paymentInfo) {
        this.reservation = reservation;
        this.paymentInfo = paymentInfo;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }
}
