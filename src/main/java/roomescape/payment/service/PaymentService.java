package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRestClient paymentRestClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment create(Payment payment) {
        paymentRestClient.approve(payment);
        return paymentRepository.save(payment);
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 결제 정보가 존재하지 않습니다."));
    }
}
