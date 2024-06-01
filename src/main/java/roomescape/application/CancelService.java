package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.domain.event.CancelEventPublisher;
import roomescape.domain.payment.CancelReason;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;

@Service
@RequiredArgsConstructor
public class CancelService {
    private final ReservationRepository reservationRepository;
    private final CancelEventPublisher eventPublisher;
    private final PaymentClient paymentClient;

    @Transactional
    public void cancelReservation(Long reservationId, MemberInfo memberInfo) {
        Reservation reservation = reservationRepository.getReservation(reservationId);
        reservation.cancel(memberInfo.id());
        reservation.getPayment().ifPresent(payment -> paymentClient.cancel(payment, CancelReason.empty()));
        updateFirstWaitingToPending(reservation);
    }

    @Transactional
    public void cancelReservationByAdmin(Long reservationId) {
        Reservation reservation = reservationRepository.getReservation(reservationId);
        reservation.cancelByAdmin();
        reservation.getPayment().ifPresent(payment -> paymentClient.cancel(payment, CancelReason.empty()));
        updateFirstWaitingToPending(reservation);
    }

    private void updateFirstWaitingToPending(Reservation reservation) {
        reservationRepository.findNextWaiting(reservation.getTheme(), reservation.getDate(), reservation.getTime())
                .ifPresent(nextReservation -> {
                    nextReservation.toPending();
                    eventPublisher.publishPaymentPendingEvent(nextReservation);
                });
    }
}
