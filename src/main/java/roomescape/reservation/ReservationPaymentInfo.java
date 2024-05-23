package roomescape.reservation;

public class ReservationPaymentInfo {
    private Reservation reservation;
    private String paymentKey;
    private Long totalAmount;

    public ReservationPaymentInfo(Reservation reservation, String paymentKey, Long totalAmount) {
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }
}
