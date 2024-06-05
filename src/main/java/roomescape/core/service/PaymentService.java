package roomescape.core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.core.domain.Payment;
import roomescape.core.domain.Reservation;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.payment.TossPaymentResponse;
import roomescape.core.repository.PaymentRepository;
import roomescape.core.repository.ReservationRepository;
import roomescape.infrastructure.PaymentAuthorizationProvider;

@Service
public class PaymentService {

    private final PaymentAuthorizationProvider paymentAuthorizationProvider;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(final PaymentAuthorizationProvider paymentAuthorizationProvider,
                          final PaymentRepository paymentRepository,
                          final ReservationRepository reservationRepository) {
        this.paymentAuthorizationProvider = paymentAuthorizationProvider;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public PaymentAuthorizationResponse createPaymentAuthorization() {
        return new PaymentAuthorizationResponse(paymentAuthorizationProvider.getAuthorization());
    }

    @Transactional
    public void save(final PaymentRequest paymentRequest) {
        final Reservation reservation = getReservation(paymentRequest);
        final Payment payment = new Payment(
                paymentRequest.getPaymentKey(), paymentRequest.getAmount(), paymentRequest.getOrderId(), reservation);
        paymentRepository.save(payment);
    }

    private Reservation getReservation(final PaymentRequest paymentRequest) {
        return reservationRepository.findReservationById(paymentRequest.getReservationId());
    }

    @Transactional(readOnly = true)
    public TossPaymentResponse findPaymentByReservationId(final Long id) {
        final Payment payment = paymentRepository.findByReservationId(id);
        return new TossPaymentResponse(payment.getPaymentKey());
    }

    @Transactional(readOnly = true)
    public Boolean existPaymentByReservationId(final Long id) {
        return paymentRepository.existsByReservationId(id);
    }
}
