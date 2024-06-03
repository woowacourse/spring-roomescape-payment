package roomescape.paymenthistory;

public enum PaymentType {

    NORMAL("일반 결제"),
    BRANDPAY("브랜드페이 결제"),
    KEYIN("키인 결제");

    private final String name;

    PaymentType(String name) {
        this.name = name;
    }
}
