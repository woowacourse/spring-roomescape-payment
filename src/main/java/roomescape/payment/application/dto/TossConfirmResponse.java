package roomescape.payment.application.dto;

public record TossConfirmResponse(
    String paymentKey,
    String orderId,
    EasyPayInfo easyPay
) {

    public record EasyPayInfo(
        Long amount
    ) {

    }
}
