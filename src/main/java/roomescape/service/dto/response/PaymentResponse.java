package roomescape.service.dto.response;

import roomescape.domain.payment.Payment;

import java.math.BigDecimal;

public record PaymentResponse(
        String paymentKey,
        BigDecimal amount,
        String paymentType,
        String accountNumber,
        String accountHolder,
        String bankName
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentKey(),
                payment.getAmount(),
                payment.getPayType().getDescription(),
                payment.getAccountNumber(),
                payment.getAccountHolder(),
                payment.getBankName()
        );
    }

    public static PaymentResponse empty() {
        return new PaymentResponse(null, null, null, null, null, null);
    }
}
