package roomescape.reservation.controller.dto;

import roomescape.payment.domain.PaymentHistory;

public record PaymentHistoryWebResponse(String paymentKey, int amount) {

    public PaymentHistoryWebResponse(PaymentHistory paymentHistory) {
        this(paymentHistory.getPaymentKey(), paymentHistory.getAmount());
    }
}
