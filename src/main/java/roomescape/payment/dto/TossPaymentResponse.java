package roomescape.payment.dto;

public record TossPaymentResponse(
        String mId,
        String lastTransactionKey,
        String paymentKey,
        String orderId,
        String orderName,
        Long taxExemptionAmount,
        String status,
        Long totalAmount,
        EasyPay easyPay

) {
    private record EasyPay(String provider, Long amount, Long discountAmount) {
    }
}
