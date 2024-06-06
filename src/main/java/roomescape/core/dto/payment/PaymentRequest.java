package roomescape.core.dto.payment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import roomescape.core.dto.reservation.ReservationPaymentRequest;

public class PaymentRequest {
    @NotNull(message = "예약 ID는 비어있을 수 없습니다.")
    private Long reservationId;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @NotNull(message = "결제 금액은 비어있을 수 없습니다.")
    private Integer amount;

    @NotNull(message = "orderId는 비어있을 수 없습니다.")
    private String orderId;

    @NotNull(message = "paymentKey는 비어있을 수 없습니다.")
    private String paymentKey;

    public PaymentRequest() {
    }

    public PaymentRequest(final Long reservationId, ReservationPaymentRequest request) {
        this(reservationId, request.getAmount(), request.getOrderId(), request.getPaymentKey());
    }

    public PaymentRequest(final Long reservationId, final Integer amount, final String orderId,
                          final String paymentKey) {
        this.reservationId = reservationId;
        this.amount = amount;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
    }

    public @NotNull(message = "예약 ID는 비어있을 수 없습니다.") Long getReservationId() {
        return reservationId;
    }

    public @Min(0) @Max(Integer.MAX_VALUE) @NotNull(message = "결제 금액은 비어있을 수 없습니다.") Integer getAmount() {
        return amount;
    }

    public @NotNull(message = "orderId는 비어있을 수 없습니다.") String getOrderId() {
        return orderId;
    }

    public @NotNull(message = "paymentKey는 비어있을 수 없습니다.") String getPaymentKey() {
        return paymentKey;
    }
}
