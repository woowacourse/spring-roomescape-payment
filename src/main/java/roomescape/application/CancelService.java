package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.domain.event.CancelEventPublisher;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.exception.AuthorizationException;

@Service
@RequiredArgsConstructor
public class CancelService {
    private final ReservationRepository reservationRepository;
    private final CancelEventPublisher eventPublisher;
    private final PaymentClient paymentClient;

    @Transactional
    public void cancelReservation(Long reservationId, MemberInfo memberInfo) {
        Reservation reservation = reservationRepository.getById(reservationId);
        rejectIfNotOwnerOrAdmin(reservation, memberInfo);
        reservation.toCancel();
        updateFirstWaitingToPending(reservation);
    }

    private void rejectIfNotOwnerOrAdmin(Reservation reservation, MemberInfo memberInfo) {
        if (reservation.isNotOwner(memberInfo.id())) {// && !memberInfo.isAdmin()) {
            throw new AuthorizationException();
        }
    }

    private void updateFirstWaitingToPending(Reservation reservation) {
        reservationRepository.findNextWaiting(reservation.getDetail())
                .ifPresent(nextReservation -> {
                    nextReservation.toPending();
                    eventPublisher.publishPaymentPendingEvent(nextReservation);
                });
    }
}
