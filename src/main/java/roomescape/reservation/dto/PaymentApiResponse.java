package roomescape.reservation.dto;

import org.springframework.http.HttpStatus;
import roomescape.exception.PaymentException;
import roomescape.reservation.model.Payment;

public record PaymentApiResponse(String paymentKey, String orderId, Long totalAmount, String status) {

    public Payment toEntity(PaymentRequest paymentRequest) {
        validateDataIntegrity(paymentRequest);
        return new Payment(paymentKey, orderId, status, totalAmount);
    }

    private void validateDataIntegrity(PaymentRequest paymentRequest) {
        if (!paymentKey.equals(paymentRequest.paymentKey())) {
            throwPaymentException("paymentKey:" + paymentRequest.paymentKey());
        }
        if (!orderId.equals(paymentRequest.orderId())) {
            throwPaymentException("orderId:" + paymentRequest.orderId());
        }
        if (!totalAmount.equals(paymentRequest.amount())) {
            throwPaymentException("amount:" + paymentRequest.amount());
        }
    }

    private void throwPaymentException(String data) {
        throw new PaymentException("올바르지 않은 요청 데이터가 입력되었습니다. " + data, HttpStatus.BAD_REQUEST);
    }
}
