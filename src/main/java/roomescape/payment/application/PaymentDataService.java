package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.ProductType;
import roomescape.payment.exception.PaymentKeyDuplicatedException;
import roomescape.payment.infrastructure.PaymentRepository;

@Service
public class PaymentDataService {

    private final PaymentRepository paymentRepository;

    public PaymentDataService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment save(Payment payment) {
        if (paymentRepository.existsByPaymentKey(payment.getPaymentKey())) {
            throw new PaymentKeyDuplicatedException("중복된 paymentKey입니다.");
        }
        return paymentRepository.save(payment);
    }

    public Payment findByProductTypeAndProductId(ProductType productType, Long productId) {
        return paymentRepository.findByProductTypeAndProductId(productType, productId);
    }
}
