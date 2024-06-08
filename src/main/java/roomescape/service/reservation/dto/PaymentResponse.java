package roomescape.service.reservation.dto;

import roomescape.domain.payment.Payment;

public record PaymentResponse(String paymentKey, long amount) {
    public static PaymentResponse of(Payment payment) {
        if(payment == null){
            return new PaymentResponse("", 0);
        }
        return new PaymentResponse(payment.getPaymentKey(), payment.getAmount());
    }
}
