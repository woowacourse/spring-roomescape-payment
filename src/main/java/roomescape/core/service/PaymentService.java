package roomescape.core.service;

import org.springframework.stereotype.Service;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.payment.PaymentResponse;
import roomescape.core.repository.PaymentRepository;
import roomescape.infrastructure.PaymentAuthorizationProvider;
import roomescape.infrastructure.PaymentClient;

@Service
public class PaymentService {

    private final PaymentAuthorizationProvider paymentAuthorizationProvider;
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(
            PaymentAuthorizationProvider paymentAuthorizationProvider,
            PaymentRepository paymentRepository,
            PaymentClient paymentClient
    ) {
        this.paymentAuthorizationProvider = paymentAuthorizationProvider;
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public PaymentResponse approvePayment(final PaymentRequest paymentRequest) {
        paymentClient.approvePayment(paymentRequest, createPaymentAuthorization());

        return new PaymentResponse(paymentRepository.save(paymentRequest.toPayment()));
    }

    public PaymentAuthorizationResponse createPaymentAuthorization() {
        return new PaymentAuthorizationResponse(paymentAuthorizationProvider.getAuthorization());
    }

    public void refundPayment(PaymentResponse paymentResponse) {
        paymentClient.refundPayment(paymentResponse, createPaymentAuthorization());
        paymentRepository.delete(paymentResponse.toPayment());
    }
}
