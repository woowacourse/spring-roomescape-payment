package roomescape.domain.payment;

public enum PaymentStatus {
    DONE,
    CANCELED;

    public static PaymentStatus from(String status) {
        for (PaymentStatus value : values()) {
            if (value.name().equalsIgnoreCase(status)) {
                return value;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 결제 상태입니다.");
    }
}
