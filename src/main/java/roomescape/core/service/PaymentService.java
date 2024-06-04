package roomescape.core.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import roomescape.core.domain.Payment;
import roomescape.core.domain.PaymentStatus;
import roomescape.core.domain.Reservation;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.repository.PaymentRepository;
import roomescape.core.repository.ReservationRepository;
import roomescape.infrastructure.PaymentClient;

@Service
public class PaymentService {
    protected static final String MEMBER_NOT_EXISTS_EXCEPTION_MESSAGE = "존재하지 않는 회원입니다.";

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentClient paymentClient;

    public PaymentService(final PaymentRepository paymentRepository, final ReservationRepository reservationRepository,
                          final PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public PaymentConfirmResponse confirmPayment(final ReservationResponse reservationResponse,
                                                 final ReservationPaymentRequest paymentRequest) {
        final Reservation reservation = reservationRepository.findById(reservationResponse.getId())
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_EXISTS_EXCEPTION_MESSAGE));

        final PaymentConfirmResponse response = paymentClient.getPaymentConfirmResponse(paymentRequest);
        final Payment payment = new Payment(reservation, response.getPaymentKey(), response.getOrderId(),
                response.getTotalAmount(), PaymentStatus.CONFIRMED);

        paymentRepository.save(payment);

        return response;
    }
}
