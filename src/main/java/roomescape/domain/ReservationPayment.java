package roomescape.domain;

public class ReservationPayment {

    private final Reservation reservation;
    private final Long paymentId;
    private final String paymentKey;
    private final String orderId;
    private final Long totalAmount;

    public ReservationPayment(Payment payment) {
        this(payment.getReservation(), payment.getId(), payment.getPaymentKey(), payment.getOrderId(), payment.getTotalAmount());
    }

    public ReservationPayment(Reservation reservation, Long paymentId, String paymentKey, String orderId, Long totalAmount) {
        this.reservation = reservation;
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }
}
