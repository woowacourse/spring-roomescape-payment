package roomescape.domain.payment;

public enum PayType {
    ACCOUNT_TRANSFER("계좌이체"),
    TOSS_PAY("토스페이");

    private final String description;

    PayType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
