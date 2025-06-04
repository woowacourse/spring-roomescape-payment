package roomescape.payment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void approvePayment(final PaymentInfo paymentInfo) {
        paymentClient.approvePayment(paymentInfo);
        paymentRepository.save(paymentInfo);
    }
}
