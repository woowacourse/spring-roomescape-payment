package roomescape.core.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.core.dto.reservation.MemberReservationRequest;

public class PaymentRequest {
    @NotBlank(message = "paymentKey 는 비어있을 수 없습니다.")
    private String paymentKey;

    @NotBlank(message = "orderId 는 비어있을 수 없습니다.")
    private String orderId;

    @NotNull(message = "amount 는 null일 수 없습니다.")
    private Long amount;

    public PaymentRequest(String paymentKey, String orderId, Long amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public PaymentRequest(MemberReservationRequest memberReservationRequest) {
        this(
                memberReservationRequest.getPaymentKey(),
                memberReservationRequest.getOrderId(),
                memberReservationRequest.getAmount()
        );
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
}
