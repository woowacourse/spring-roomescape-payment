package roomescape.payment.dto;

import roomescape.member.model.Member;
import roomescape.payment.model.PaymentHistory;
import roomescape.payment.model.PaymentStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

public class PaymentConfirmResponse {

    private final String orderId;
    private final String status;
    private final String orderName;
    private final Long totalAmount;
    private final LocalDateTime approvedAt;
    private final String paymentProvider;

    public PaymentConfirmResponse(
            final String orderId,
            final String status,
            final String orderName,
            final Long totalAmount,
            final String approvedAt,
            final Map<String, String> easyPay
    ) {
        this.orderId = orderId;
        this.status = status;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.approvedAt = convertLocalDateTime(approvedAt);
        this.paymentProvider = easyPay.get("provider");
    }

    private LocalDateTime convertLocalDateTime(final String approvedAt) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(approvedAt);
        return offsetDateTime.toLocalDateTime();
    }

    public PaymentHistory toPaymentHistory(final Member member) {
        return new PaymentHistory(
                orderId,
                PaymentStatus.valueOf(status),
                orderName,
                totalAmount,
                approvedAt,
                paymentProvider,
                member
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

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }
}
