package roomescape.reservation.domain;

public enum PaymentStatus {

    PENDING("결제 대기"),
    SUCCESS("예약 완료");

    private final String name;

    PaymentStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
