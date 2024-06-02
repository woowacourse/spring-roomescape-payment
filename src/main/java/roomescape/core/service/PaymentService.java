package roomescape.core.service;

import org.springframework.stereotype.Service;
import roomescape.core.domain.Payment;
import roomescape.core.domain.Reservation;
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

    public PaymentService(final PaymentAuthorizationProvider paymentAuthorizationProvider,
                          final PaymentRepository paymentRepository, final PaymentClient paymentClient) {
        this.paymentAuthorizationProvider = paymentAuthorizationProvider;
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public PaymentResponse approvePayment(final Reservation reservation, final PaymentRequest paymentRequest) {
        paymentClient.approvePayment(paymentRequest, createPaymentAuthorization());

        final Payment payment = new Payment(reservation, paymentRequest.getPaymentKey(), paymentRequest.getAmount(),
                paymentRequest.getOrderId());

        return new PaymentResponse(paymentRepository.save(payment));
    }

    public PaymentAuthorizationResponse createPaymentAuthorization() {
        return new PaymentAuthorizationResponse(paymentAuthorizationProvider.getAuthorization());
    }

    public void refundPayment(Reservation reservation) {
        paymentRepository.findByReservation(reservation).ifPresent(
                payment -> {
                    paymentClient.refundPayment(new PaymentResponse(payment), createPaymentAuthorization());
                    paymentRepository.delete(payment);
                }
        );
    }
}
