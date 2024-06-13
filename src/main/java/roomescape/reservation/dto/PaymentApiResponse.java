package roomescape.reservation.dto;

import roomescape.reservation.model.Payment;

public record PaymentApiResponse(String status, String orderId, String paymentKey, Long totalAmount) {

    public Payment toEntity() {
        return new Payment(status, orderId, paymentKey, totalAmount);
    }
}
