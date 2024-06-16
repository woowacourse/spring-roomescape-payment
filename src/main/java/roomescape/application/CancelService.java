package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.domain.event.CancelEventPublisher;
import roomescape.domain.payment.CancelReason;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.reservation.Reservation;
import roomescape.infrastructure.repository.ReservationRepository;

@Service
@RequiredArgsConstructor
public class CancelService {
    private final ReservationRepository reservationRepository;
    private final CancelEventPublisher eventPublisher;
    private final PaymentClient paymentClient;

    @Transactional
    public void cancelReservation(Long reservationId, MemberInfo memberInfo) {
        Reservation reservation = reservationRepository.getReservationById(reservationId);
        reservation.validateOwner(memberInfo.id());
        cancelReservation(reservation);
    }

    @Transactional
    public void cancelReservationByAdmin(Long reservationId) {
        Reservation reservation = reservationRepository.getReservationById(reservationId);
        cancelReservation(reservation);
    }

    @Transactional
    public void updateFirstWaitingToPending(Long reservationId) {
        Reservation reservation = reservationRepository.getReservationById(reservationId);
        reservationRepository.findNextWaiting(reservation.getTheme(), reservation.getDate(), reservation.getTime())
                .ifPresent(nextReservation -> {
                    nextReservation.toPending();
                    eventPublisher.publishPaymentPendingEvent(nextReservation);
                });
    }

    private void cancelReservation(Reservation reservation) {
        reservation.cancel();
        reservation.getPayment().ifPresent(payment -> paymentClient.cancel(payment, CancelReason.empty()));
    }
}
