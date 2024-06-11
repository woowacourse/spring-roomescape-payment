package roomescape.domain.payment.dto;

import roomescape.domain.payment.model.PaymentHistory;
import roomescape.domain.payment.model.PaymentStatus;
import roomescape.domain.reservation.model.Reservation;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

public class PaymentConfirmResponse {
    private final String orderId;
    private final String status;
    private final String orderName;
    private final Long totalAmount;
    private final LocalDateTime approvedAt;
    private final String paymentKey;
    private final String paymentProvider;

    public PaymentConfirmResponse(
            final String orderId,
            final String status,
            final String orderName,
            final Long totalAmount,
            final String approvedAt,
            final String paymentKey,
            final Map<String, String> easyPay
    ) {
        this.orderId = orderId;
        this.status = status;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.approvedAt = convertLocalDateTime(approvedAt);
        this.paymentKey = paymentKey;
        this.paymentProvider = easyPay.get("provider");
    }

    private LocalDateTime convertLocalDateTime(final String approvedAt) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(approvedAt);
        return offsetDateTime.toLocalDateTime();
    }

    public PaymentHistory toModel(final Reservation reservation) {
        return new PaymentHistory(
                orderId,
                PaymentStatus.valueOf(status),
                orderName,
                totalAmount,
                approvedAt,
                paymentKey,
                paymentProvider,
                reservation
        );
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getOrderName() {
        return orderName;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }
}
