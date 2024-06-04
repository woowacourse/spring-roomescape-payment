package roomescape.domain;

import java.time.LocalDate;

public class PaidReservation {

    private final Reservation reservation;
    private final Long paymentId;
    private final String paymentKey;
    private final String orderId;
    private final Long totalAmount;

    public PaidReservation(Payment payment) {
        this.reservation = payment.getReservation();
        this.paymentId = payment.getId();
        this.paymentKey = payment.getPaymentKey();
        this.orderId = payment.getOrderId();
        this.totalAmount = payment.getTotalAmount();
    }

    public Long getId() {
        return reservation.getId();
    }

    public String getName() {
        Member member = reservation.getMember();
        MemberName memberName = member.getName();

        return memberName.getName();
    }

    public LocalDate getDate() {
        ReservationDate reservationDate = reservation.getDate();

        return reservationDate.getDate();
    }

    public ReservationTime getTime() {
        return reservation.getTime();
    }

    public Theme getTheme() {
        return reservation.getTheme();
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
