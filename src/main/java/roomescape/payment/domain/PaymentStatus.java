package roomescape.payment.domain;

public enum PaymentStatus {
    COMPLETED("결제완료"),
    CANCELED("결제취소"),
    REFUNDED("환불완료");


    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
