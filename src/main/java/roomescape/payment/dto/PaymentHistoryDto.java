package roomescape.payment.dto;

import roomescape.payment.model.PaymentHistory;

public record PaymentHistoryDto(Long paymentHistoryId, String paymentKey, Long totalAmount) {

    public static PaymentHistoryDto from(PaymentHistory paymentHistory) {
        return new PaymentHistoryDto(
                paymentHistory.getId(),
                paymentHistory.getPaymentKey(),
                paymentHistory.getTotalAmount()
        );
    }

    public static PaymentHistoryDto getDefault() {
        return new PaymentHistoryDto(null, null, null);
    }
}
