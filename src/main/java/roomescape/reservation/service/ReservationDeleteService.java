package roomescape.reservation.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.BadArgumentRequestException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentClient;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepository;

@Service
public class ReservationDeleteService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public ReservationDeleteService(ReservationRepository reservationRepository,
                                    WaitingRepository waitingRepository,
                                    PaymentRepository paymentRepository,
                                    PaymentClient paymentClient) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public void deleteReservation(Long reservationId) {
        Reservation reservation = findReservation(reservationId);
        validateIsAfterFromNow(reservation);

        refund(reservation);
        findHighPriorityWaiting(reservationId).ifPresentOrElse(
                waiting -> confirmReservation(reservation, waiting),
                () -> reservationRepository.delete(reservation));
    }

    private Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new BadArgumentRequestException("해당 예약을 찾을 수 없습니다."));
    }

    private void validateIsAfterFromNow(Reservation reservation) {
        if (reservation.isBefore(LocalDateTime.now())) {
            throw new BadArgumentRequestException("예약은 현재 날짜 이후여야 합니다.");
        }
    }

    private void refund(Reservation reservation) {
        if (reservation.canRefund()) {
            Payment payment = paymentRepository.findByScheduleAndMemberAndStatus(
                            reservation.getSchedule(), reservation.getMember(), PaymentStatus.PAID)
                    .orElseThrow(() -> new BadArgumentRequestException("해당 예약 내역이 존재하지 않습니다."));
            paymentClient.refundPayment(payment.getPaymentKey());
            payment.completeRefund();
        }
    }

    private Optional<Waiting> findHighPriorityWaiting(Long reservationId) {
        return waitingRepository.findTopByScheduleIdOrderByCreatedAtAsc(reservationId);
    }

    private void confirmReservation(Reservation reservation, Waiting waiting) {
        reservation.confirm(waiting.getMember());
        waitingRepository.delete(waiting);
    }
}
