package roomescape.core.dto.payment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import roomescape.core.dto.reservation.ReservationPaymentRequest;

public class PaymentConfirmRequest {
    @Min(0)
    @Max(Integer.MAX_VALUE)
    @NotNull(message = "결제 금액은 비어있을 수 없습니다.")
    private Integer amount;

    @NotNull(message = "orderId는 비어있을 수 없습니다.")
    private String orderId;

    @NotNull(message = "paymentKey는 비어있을 수 없습니다.")
    private String paymentKey;

    public PaymentConfirmRequest(final ReservationPaymentRequest request) {
        this.amount = request.getAmount();
        this.orderId = request.getOrderId();
        this.paymentKey = request.getPaymentKey();
    }

    public PaymentConfirmRequest() {
    }

    public Integer getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
