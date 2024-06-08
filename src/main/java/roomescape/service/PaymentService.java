package roomescape.service;

import static roomescape.exception.RoomescapeExceptionCode.REQUEST_TIMEOUT;
import static roomescape.exception.RoomescapeExceptionCode.RESERVATION_NOT_FOUND;

import org.springframework.stereotype.Service;

import roomescape.component.TossPaymentClient;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.exception.RoomescapeException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
public class PaymentService {

    private final TossPaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(
            final TossPaymentClient paymentClient,
            final PaymentRepository paymentRepository,
            final ReservationRepository reservationRepository
    ) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public void confirm(final PaymentConfirmRequest request) {
        final Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new RoomescapeException(RESERVATION_NOT_FOUND));
        final Payment payment = request.toPayment(reservation);
        try {
            paymentClient.confirm(request);
        } catch (Exception e) {
            throw new RoomescapeException(REQUEST_TIMEOUT);
        }
        paymentRepository.save(payment);
    }
}
