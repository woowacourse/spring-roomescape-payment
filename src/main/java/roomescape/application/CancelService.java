package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.domain.event.CancelEventPublisher;
import roomescape.domain.payment.CancelReason;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;

@Service
@RequiredArgsConstructor
public class CancelService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final CancelEventPublisher eventPublisher;
    private final PaymentClient paymentClient;

    @Transactional
    public void cancelReservation(Long reservationId, MemberInfo memberInfo) {
        Reservation reservation = reservationRepository.getById(reservationId);
        reservation.cancel(memberInfo.id());
        paymentClient.cancel(reservation.getPayment(), CancelReason.empty());
        updateFirstWaitingToPending(reservation);
    }

    @Transactional
    public void forceCancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.getById(reservationId);
        reservation.forceCancel();
        paymentClient.cancel(reservation.getPayment(), CancelReason.empty());
        updateFirstWaitingToPending(reservation);
    }

    private void updateFirstWaitingToPending(Reservation reservation) {
        reservationRepository.findNextWaitingReservation(reservation.getDetail())
                .ifPresent(nextReservation -> {
                    nextReservation.toPending();
                    eventPublisher.publishPaymentPendingEvent(nextReservation);
                });
    }
}
