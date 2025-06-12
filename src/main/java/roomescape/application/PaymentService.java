package roomescape.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.OrderId;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentKey;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.TransactionStatus;
import roomescape.domain.payment.TransactionStatusCode;
import roomescape.exception.PaymentFailedException;
import roomescape.exception.PaymentInternalException;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentProvider paymentProvider;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment register(String paymentKey, String orderId, long totalAmount, String status) {
        var payment = new Payment(
            new PaymentKey(paymentKey),
            new OrderId(orderId),
            totalAmount,
            PaymentStatus.valueOf(status)
        );
        log.info("결제 정보 DB에 저장. paymentKey={}, orderId={}, totalAmount={}, status={}", paymentKey, orderId, totalAmount, status);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment pay(final String paymentKey, final String orderId, final long amount) {
        var request = new PaymentRequest(paymentKey, orderId, amount);

        var paymentDetails = paymentProvider.confirm(request);
        if (paymentDetails.isFailed()) {
            throwPaymentException(paymentDetails.status());
        }

        var confirmation = paymentDetails.confirmation();
        return register(confirmation.paymentKey(), confirmation.orderId(), confirmation.totalAmount(), confirmation.status());
    }

    private void throwPaymentException(final TransactionStatus status) {
        if (TransactionStatusCode.FAILED_PAYMENT.equals(status.code())) {
            log.warn("[예외 발생] 결제 조건 불만족으로 결제 실패");
            throw new PaymentFailedException(status.message());
        }
        log.error("[예외 발생] 서버 내부 오류로 결제 실패");
        throw new PaymentInternalException(status.message());
    }
}
