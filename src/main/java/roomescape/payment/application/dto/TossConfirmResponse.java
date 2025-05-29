package roomescape.payment.application.dto;

public record TossConfirmResponse(
    String paymentKey,
    String orderId, // amount <- 요청
    EasyPayInfo easyPay
) {

    public record EasyPayInfo(
        Long amount
    ) {

    }
}