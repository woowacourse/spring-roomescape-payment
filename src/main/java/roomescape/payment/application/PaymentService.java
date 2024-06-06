package roomescape.payment.application;

import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.pg.TossPaymentsClient;
import roomescape.payment.pg.TossPaymentsConfirmRequest;

class PaymentService {
    private final TossPaymentsClient client;
    private final PaymentRepository repository;

    public PaymentService(TossPaymentsClient client, PaymentRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    public void confirm(PaymentConfirmRequest request) {
        TossPaymentsConfirmRequest tossReq = new TossPaymentsConfirmRequest(request);
        Payment payment = createPayment(tossReq);
        repository.save(payment);
//        client.confirm(tossReq);
    }

    private Payment createPayment(TossPaymentsConfirmRequest tossReq) {
        return new Payment();
    }
}
