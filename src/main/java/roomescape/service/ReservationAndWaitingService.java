package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.repository.PaymentRepository;
import roomescape.domain.repository.ReservationRepository;
import roomescape.domain.repository.ReservationWaitingRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationDate;
import roomescape.domain.reservation.ReservationWaiting;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentCancelDto;

import java.util.Optional;

@Service
public class ReservationAndWaitingService {

    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentManager paymentManager;

    public ReservationAndWaitingService(ReservationRepository reservationRepository, ReservationWaitingRepository reservationWaitingRepository,
                                        PaymentRepository paymentRepository, PaymentManager paymentManager) {
        this.reservationRepository = reservationRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.paymentRepository = paymentRepository;
        this.paymentManager = paymentManager;
    }

    @Transactional
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("예약 삭제 실패: 존재하지 않는 예약입니다. (id: %d)", reservationId)));
        paymentRepository.findByReservationId(reservationId)
                .ifPresent(this::cancelPayment);
        reservationRepository.deleteById(reservationId);
        findWaitingOfReservation(reservation).ifPresent(this::changeWaitingToReservation);
    }

    private void cancelPayment(Payment payment) {
        PaymentCancelDto paymentCancelDto = new PaymentCancelDto("사용자가 방탈출 예약을 삭제했습니다.");
        paymentManager.cancel(payment.getPaymentKey(), paymentCancelDto);
        paymentRepository.deleteById(payment.getId());
    }

    private Optional<ReservationWaiting> findWaitingOfReservation(Reservation reservation) {
        ReservationDate date = reservation.getDate();
        Long timeId = reservation.getTime().getId();
        Long themeId = reservation.getTheme().getId();
        return reservationWaitingRepository.findTopByDateAndTimeIdAndThemeIdOrderById(date, timeId, themeId);
    }

    private void changeWaitingToReservation(ReservationWaiting waiting) {
        Reservation reservation = new Reservation(
                waiting.getMember(),
                waiting.getDate(),
                waiting.getTime(),
                waiting.getTheme()
        );
        reservationWaitingRepository.deleteById(waiting.getId());
        reservationRepository.save(reservation);
    }
}
