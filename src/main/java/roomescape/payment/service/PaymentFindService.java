package roomescape.payment.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentFindService {
    private final PaymentRepository paymentRepository;

    public PaymentFindService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentResponse> findPayment(Long memberId) {
        return paymentRepository.findByMemberId(memberId).stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
