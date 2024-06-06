package roomescape.service;

import static roomescape.exception.RoomescapeExceptionCode.DATABASE_SAVE_ERROR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.component.TossPaymentClient;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentDto;
import roomescape.exception.RoomescapeException;
import roomescape.exception.RoomescapeExceptionCode;
import roomescape.exception.TossPaymentsException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Transactional
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

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

    @Retryable(include = { TossPaymentsException.class }, maxAttempts = 3)
    public void confirmPayment(final PaymentDto paymentDto, final Long reservationId) {
        paymentClient.confirm(paymentDto);
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomescapeException(RoomescapeExceptionCode.RESERVATION_NOT_FOUND));
        final Payment payment = paymentDto.toPayment(reservation, PaymentStatus.PAID);
        try {
            paymentRepository.save(payment);
        } catch (RuntimeException e) {
            logger.error("Failed to save payment reservationId = {}: {}", reservationId, e.getMessage());
            paymentClient.cancel(paymentDto, DATABASE_SAVE_ERROR.getMessage());
        }
    }

    public void createPayment(final Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomescapeException(RoomescapeExceptionCode.RESERVATION_NOT_FOUND));
        final Payment payment = new Payment(reservation, PaymentStatus.PAID);
        paymentRepository.save(payment);
    }

    public void payForReservation(final PaymentDto paymentDto, final Long reservationId) {
        paymentClient.confirm(paymentDto);
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomescapeException(RoomescapeExceptionCode.RESERVATION_NOT_FOUND));
        reservation.toReserved();

        final Payment payment = paymentDto.toPayment(reservation, PaymentStatus.PAID);
        paymentRepository.save(payment);
    }

    public void cancelPayment(final Reservation reservation) {
        final Payment payment = paymentRepository.findByReservationAndStatus(reservation, PaymentStatus.PAID)
                .orElseThrow(() -> new RoomescapeException(RoomescapeExceptionCode.PAYMENT_NOT_FOUND));
        payment.toCanceled();
        final PaymentDto paymentDto = new PaymentDto(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
        paymentClient.cancel(paymentDto, "고객 변심");
    }
}
