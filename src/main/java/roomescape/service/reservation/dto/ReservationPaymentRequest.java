package roomescape.service.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.service.payment.dto.PaymentConfirmInput;

public class ReservationPaymentRequest {
    @NotBlank(message = "paymentKey 값이 null 또는 공백일 수 없습니다.")
    private final String paymentKey;
    @NotBlank(message = "orderId 값이 null 또는 공백일 수 없습니다.")
    private final String orderId;
    @NotNull(message = "amount 값이 null일 수 없습니다.")
    private final Integer amount;

    public ReservationPaymentRequest(String paymentKey, String orderId, Integer amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public PaymentConfirmInput toPaymentConfirmInput() {
        return new PaymentConfirmInput(orderId, amount, paymentKey);
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getAmount() {
        return amount;
    }
}
