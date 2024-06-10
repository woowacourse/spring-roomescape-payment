package roomescape.payment.domain;

import java.math.BigDecimal;

import roomescape.payment.dto.EasyPayTypeDetail;

public record PaymentInfo(
        String orderName,
        String paymentKey,
        String requestedAt,
        String approvedAt,
        EasyPayTypeDetail easyPay,
        String currency,
        BigDecimal totalAmount) {
}
