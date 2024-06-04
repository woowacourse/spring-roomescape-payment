package roomescape.domain.payment;

public enum PaymentType {
    NORMAL, BILLING, BRANDPAY, ADMIN;

    public boolean isByAdmin() {
        return this == ADMIN;
    }
}
