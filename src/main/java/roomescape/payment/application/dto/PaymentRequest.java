package roomescape.payment.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotBlank(message = "결제 키를 반드시 입력해야 합니다")
    private final String paymentKey;

    @NotBlank(message = "주문 번호를 반드시 입력해야 합니다")
    private final String orderId;

    @NotNull(message = "주문 금액을 반드시 입력해야 합니다")
    private final Integer amount;

    @NotBlank(message = "결제 유형을 반드시 입력해야 합니다")
    private final String paymentType;

    public PaymentRequest(String paymentKey, String orderId, Integer amount, String paymentType) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentType = paymentType;
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

    public String getPaymentType() {
        return paymentType;
    }
}
