package roomescape.payment.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentProduct;
import roomescape.payment.domain.PaymentRepository;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    public PaymentService(PaymentRepository paymentRepository, PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public void pay(ProductPayRequest request, PaymentProduct product) {
        Payment payment = paymentGateway.createPayment(request.paymentKey(), product);
        paymentRepository.save(payment);
        paymentGateway.processAfterPaid(request);
    }

    public List<Payment> findAllInPaymentProducts(List<PaymentProduct> products) {
        return paymentRepository.findAllByPaymentProductIsIn(products);
    }
}
