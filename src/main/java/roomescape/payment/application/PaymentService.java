package roomescape.payment.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.ViolationException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentProduct;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.pg.TossPaymentsClient;
import roomescape.payment.pg.TossPaymentsConfirmRequest;
import roomescape.payment.pg.TossPaymentsPayment;

@Service
public class PaymentService {
    private final TossPaymentsClient client;
    private final PaymentRepository paymentRepository;

    public PaymentService(TossPaymentsClient client, PaymentRepository paymentRepository) {
        this.client = client;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void confirm(PaymentConfirmRequest request, PaymentProduct product) {
        TossPaymentsConfirmRequest tossPaymentsConfirmRequest = new TossPaymentsConfirmRequest(request);
        Payment payment = createPayment(tossPaymentsConfirmRequest, product);
        paymentRepository.save(payment);
        client.confirm(tossPaymentsConfirmRequest);
    }

    private Payment createPayment(TossPaymentsConfirmRequest request, PaymentProduct product) {
        TossPaymentsPayment payment = client.findBy(request.getPaymentKey());
        boolean verified = payment.verify(request);
        if (!verified) {
            throw new ViolationException("올바른 결제 정보를 입력해주세요.");
        }

        return new Payment(request.getPaymentKey(), request.getOrderId(), request.getAmount(), product);
    }
}
