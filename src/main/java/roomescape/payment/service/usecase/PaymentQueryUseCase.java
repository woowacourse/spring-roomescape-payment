package roomescape.payment.service.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import roomescape.common.exception.PaymentException;
import roomescape.payment.domain.PaymentVerification;
import roomescape.payment.repository.PaymentVerificationRepository;

@RequiredArgsConstructor
@Service
public class PaymentQueryUseCase {

    private final PaymentVerificationRepository paymentVerificationRepository;

    public PaymentVerification getPaymentVerificationByOrderId(final String orderId) {
        final List<PaymentVerification> paymentVerifications = paymentVerificationRepository.findByOrderId(orderId);

        if (paymentVerifications.isEmpty()) {
            throw new PaymentException(HttpStatus.NOT_FOUND, "orderId가 존재하지 않습니다.");
        }

        return paymentVerifications.getFirst();
    }
}
