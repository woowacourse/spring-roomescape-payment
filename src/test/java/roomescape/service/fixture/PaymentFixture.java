package roomescape.service.fixture;

import roomescape.model.Payment;

public enum PaymentFixture {
    GENERAL(1999999L, "tgen_20240604202416eHBf1");
    private Long amount;
    private String paymentKey;

    PaymentFixture(final Long amount, final String paymentKey) {
        this.amount = amount;
        this.paymentKey = paymentKey;
    }

    public Payment getPayment() {
        return new Payment(paymentKey, amount);
    }
}
