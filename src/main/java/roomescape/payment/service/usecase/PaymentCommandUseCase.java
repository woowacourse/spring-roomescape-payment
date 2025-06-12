package roomescape.payment.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.AlreadyExistException;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentCommandUseCase {

    private final PaymentRepository paymentRepository;

    public Payment create(final Payment payment) {
        if (paymentRepository.existsByPaymentKey(payment.getPaymentKey())) {
            throw new AlreadyExistException("paymentKey에 대한 결제 내역이 이미 존재합니다.");
        }
        return paymentRepository.save(payment);
    }
}
