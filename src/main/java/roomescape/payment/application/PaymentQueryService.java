package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentProduct;
import roomescape.payment.domain.PaymentRepository;

import java.util.List;

@Service
public class PaymentQueryService {
    private final PaymentRepository paymentRepository;

    public PaymentQueryService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> findAllInPaymentProducts(List<PaymentProduct> products) {
        return paymentRepository.findAllByPaymentProductIsIn(products);
    }
}
