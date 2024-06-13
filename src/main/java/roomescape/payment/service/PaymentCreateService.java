package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.PaymentRequest;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.repository.PaymentRequestRepository;
import roomescape.reservation.domain.PaymentStatus;

@Service
public class PaymentCreateService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final PaymentRequestRepository paymentRequestRepository;

    public PaymentCreateService(PaymentClient paymentClient,
                                PaymentRepository paymentRepository,
                                PaymentRequestRepository paymentRequestRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.paymentRequestRepository = paymentRequestRepository;
    }

    public void confirmPayment(PaymentConfirmRequest request) {
        PaymentRequest paymentRequest = request.createPaymentRequest();
        paymentRequestRepository.save(paymentRequest);

        paymentClient.confirmPayment(request);
        paymentRequest.updateStatus(PaymentStatus.COMPLETED);

        paymentRepository.save(request.createPayment());
    }
}
