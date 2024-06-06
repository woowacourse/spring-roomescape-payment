package roomescape.core.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import roomescape.core.domain.Payment;
import roomescape.core.domain.Reservation;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.payment.PaymentResponse;
import roomescape.core.repository.PaymentRepository;
import roomescape.infrastructure.PaymentApproveClient;
import roomescape.infrastructure.PaymentAuthorizationProvider;
import roomescape.infrastructure.PaymentRefundClient;

@Service
public class PaymentService {

    private final PaymentAuthorizationProvider paymentAuthorizationProvider;
    private final PaymentRepository paymentRepository;
    private final PaymentApproveClient paymentApproveClient;
    private final PaymentRefundClient paymentRefundClient;

    public PaymentService(final PaymentAuthorizationProvider paymentAuthorizationProvider,
                          final PaymentRepository paymentRepository,
                          final PaymentApproveClient paymentApproveClient,
                          final PaymentRefundClient paymentRefundClient) {
        this.paymentAuthorizationProvider = paymentAuthorizationProvider;
        this.paymentRepository = paymentRepository;
        this.paymentApproveClient = paymentApproveClient;
        this.paymentRefundClient = paymentRefundClient;
    }

    public PaymentResponse approvePayment(final Reservation reservation, final PaymentRequest paymentRequest) {
        paymentApproveClient.approvePayment(paymentRequest, createPaymentAuthorization());

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
                    paymentRefundClient.refundPayment(new PaymentResponse(payment), createPaymentAuthorization());
                    paymentRepository.delete(payment);
                }
        );
    }

    public PaymentResponse findByReservation(Reservation reservation) {
        return new PaymentResponse(paymentRepository.findByReservation(reservation)
                .orElseThrow(IllegalArgumentException::new));
    }
}
