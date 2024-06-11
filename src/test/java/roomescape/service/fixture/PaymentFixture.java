package roomescape.service.fixture;

import roomescape.model.Payment;

public enum PaymentFixture {
    GENERAL(1L, "tgen_20240604202416eHBf1", 1999999L);
    private Long id;
    private String paymentKey;
    private Long amount;

    PaymentFixture(final Long id, final String paymentKey, final Long amount) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public Payment getPayment() {
        return new Payment(id, paymentKey, amount, ReservationFixture.GENERAL.getReservation());
    }
}
