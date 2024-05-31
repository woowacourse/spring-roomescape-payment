package roomescape.service;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import roomescape.component.TossPaymentClient;
import roomescape.domain.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentDto;
import roomescape.exception.RoomescapeException;
import roomescape.exception.RoomescapeExceptionCode;
import roomescape.exception.TossPaymentException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
public class PaymentService {

    private final TossPaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(final TossPaymentClient paymentClient,
                          final PaymentRepository paymentRepository,
                          final ReservationRepository reservationRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Retryable(include = { TossPaymentException.class }, maxAttempts = 3)
    public void confirmPayment(final PaymentDto paymentDto, final Long reservationId) {
        paymentClient.confirm(paymentDto);
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomescapeException(RoomescapeExceptionCode.RESERVATION_NOT_FOUND));
        final Payment payment = paymentDto.toPayment(reservation);
        paymentRepository.save(payment);
    }
}
