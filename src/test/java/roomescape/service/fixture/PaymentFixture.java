package roomescape.service.fixture;

import roomescape.model.Payment;

public enum PaymentFixture {
    GENERAL(1999999L, "tgen_20240604202416eHBf1");
    private Long totalAmount;
    private String paymentKey;

    PaymentFixture(final Long totalAmount, final String paymentKey) {
        this.totalAmount = totalAmount;
        this.paymentKey = paymentKey;
    }

    public Payment getPayment() {
        return new Payment(totalAmount, paymentKey);
    }
}
