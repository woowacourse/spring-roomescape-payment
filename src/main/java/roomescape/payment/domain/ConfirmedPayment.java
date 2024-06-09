package roomescape.payment.domain;

import roomescape.reservation.domain.Reservation;

public class ConfirmedPayment {
    private String paymentKey;
    private String orderId;
    private long totalAmount;
    private PGCompany company;

    protected ConfirmedPayment() {
    }

    public ConfirmedPayment(String paymentKey, String orderId, long totalAmount) {
        this(paymentKey, orderId, totalAmount, null);
    }

    public ConfirmedPayment(String paymentKey, String orderId, long totalAmount, PGCompany company) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.company = company;
    }

    public Payment toModel(Reservation reservation) {
        return new Payment(paymentKey, orderId, totalAmount, reservation, company);
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setCompany(PGCompany company) {
        this.company = company;
    }
}
