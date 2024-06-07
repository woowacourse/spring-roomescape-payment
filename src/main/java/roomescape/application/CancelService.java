package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.domain.event.CancelEventPublisher;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;

@Service
@RequiredArgsConstructor
public class CancelService {
    private final ReservationRepository reservationRepository;
    private final CancelEventPublisher eventPublisher;

    @Transactional
    public void cancelReservation(Long reservationId, MemberInfo memberInfo) {
        Reservation reservation = reservationRepository.getById(reservationId);
        reservation.cancel(memberInfo.id());
        updateFirstWaitingToPending(reservation);
    }

    @Transactional
    public void cancelReservationByAdmin(Long reservationId) {
        Reservation reservation = reservationRepository.getById(reservationId);
        reservation.cancelByAdmin();
        updateFirstWaitingToPending(reservation);
    }

    private void updateFirstWaitingToPending(Reservation reservation) {
        reservationRepository.findNextWaiting(reservation.getDetail())
                .ifPresent(nextReservation -> {
                    nextReservation.toPending();
                    eventPublisher.publishPaymentPendingEvent(nextReservation);
                });
    }
}
