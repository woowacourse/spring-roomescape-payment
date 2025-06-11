package roomescape.application.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.repository.PaymentRepository;

@RequiredArgsConstructor
@Service
public class CreatePaymentService {

    private final PaymentRepository paymentRepository;

    public Long register(final PaymentType type) {
        return paymentRepository.save(new Payment(type)).getId();
    }
}
