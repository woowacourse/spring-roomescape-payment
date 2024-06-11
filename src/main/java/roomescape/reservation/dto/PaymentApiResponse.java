package roomescape.reservation.dto;

import roomescape.reservation.model.Payment;

public record PaymentApiResponse(String status, String paymentKey, String orderId) {

    public Payment toEntity(Long amount) {
        return new Payment(status, paymentKey, orderId, amount);
    }
}
