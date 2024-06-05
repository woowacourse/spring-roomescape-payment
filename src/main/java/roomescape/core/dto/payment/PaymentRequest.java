package roomescape.core.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotBlank(message = "paymentKey 는 비어있을 수 없습니다.")
    private final String paymentKey;

    @NotBlank(message = "orderId 는 비어있을 수 없습니다.")
    private final String orderId;

    @NotNull(message = "amount 는 null일 수 없습니다.")
    private final Long amount;

    @NotNull(message = "reservationId 는 null일 수 없습니다.")
    private final Long reservationId;

    public PaymentRequest(String paymentKey, String orderId, Long amount, Long reservationId) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservationId = reservationId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getReservationId() {
        return reservationId;
    }
}
