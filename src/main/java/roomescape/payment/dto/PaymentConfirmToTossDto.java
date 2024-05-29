package roomescape.payment.dto;

import java.io.Serializable;

public record PaymentConfirmToTossDto(String orderId, Integer amount, String paymentKey) implements Serializable {

    public static PaymentConfirmToTossDto from(PaymentConfirmRequest paymentConfirmRequest) {
        return new PaymentConfirmToTossDto(
                paymentConfirmRequest.orderId(),
                paymentConfirmRequest.amount(),
                paymentConfirmRequest.paymentKey()
        );
    }
}
