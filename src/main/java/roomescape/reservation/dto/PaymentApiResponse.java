package roomescape.reservation.dto;

import roomescape.reservation.model.Payment;

public record PaymentApiResponse(String status, String orderId, String paymentKey) {

    public Payment toEntity(Long amount) {
        return new Payment(status, orderId, paymentKey, amount);
    }
}
