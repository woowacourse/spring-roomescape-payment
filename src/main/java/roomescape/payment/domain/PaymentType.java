package roomescape.payment.domain;

public enum PaymentType {
    CARD("카드");
    private String value;

    PaymentType(String value) {
        this.value = value;
    }
}
