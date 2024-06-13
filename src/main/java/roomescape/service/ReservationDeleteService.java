package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.CanceledReservation;
import roomescape.domain.reservation.CanceledReservationRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.exception.RoomescapeException;

@Service
@Transactional
public class ReservationDeleteService {
    private final PaymentService paymentService;
    private final ReservationRepository reservationRepository;
    private final CanceledReservationRepository canceledReservationRepository;

    public ReservationDeleteService(
            PaymentService paymentService,
            ReservationRepository reservationRepository,
            CanceledReservationRepository canceledReservationRepository
    ) {
        this.paymentService = paymentService;
        this.reservationRepository = reservationRepository;
        this.canceledReservationRepository = canceledReservationRepository;
    }

    @Transactional
    public void deleteById(long id, LoginMember loginMember) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("존재하지 않는 예약입니다. 요청 예약 id:%d", id)));
        validateDeleteReservation(reservation, loginMember);
        CanceledReservation canceledReservation = canceledReservationRepository.save(reservation.canceled());
        paymentService.deletePayment(reservation, canceledReservation);
        reservationRepository.deleteById(reservation.getId());
        updateWaitingToPaymentWaiting(reservation);
    }

    private void validateDeleteReservation(Reservation reservation, LoginMember loginMember) {
        if (reservation.isNotMyReservation(loginMember)) {
            throw new RoomescapeException("다른 사람의 예약은 삭제할 수 없습니다.");
        }
    }

    private void updateWaitingToPaymentWaiting(Reservation reservation) {
        if (isWaitingUpdatableToPaymentWaiting(reservation)) {
            reservationRepository.findFirstByDateAndTimeIdAndThemeIdAndStatus(
                    reservation.getDate(), reservation.getTime().getId(), reservation.getTheme().getId(), Status.WAITING
            ).ifPresent(Reservation::changePaymentWaiting);
        }
    }

    private boolean isWaitingUpdatableToPaymentWaiting(Reservation reservation) {
        return reservation.isReserved() && reservationRepository.existsByDateAndTimeIdAndThemeIdAndStatus(
                        reservation.getDate(), reservation.getTime().getId(),
                        reservation.getTheme().getId(), Status.WAITING
                );
    }
}
