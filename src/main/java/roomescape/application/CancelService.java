package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.domain.event.TimeoutEventPublisher;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.CancelReason;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.exception.AuthenticationException;
import roomescape.exception.AuthorizationException;

@Service
@RequiredArgsConstructor
public class CancelService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final TimeoutEventPublisher eventPublisher;
    private final PaymentClient paymentClient;

    @Transactional
    public void cancelReservation(Long reservationId, MemberInfo memberInfo) {
        Member member = memberRepository.findById(memberInfo.getId()).orElseThrow(AuthenticationException::new);
        Reservation reservation = reservationRepository.getById(reservationId);
        rejectIfNotAuthorized(reservation, member);
        reservation.toCancel();
        updateFirstWaitingToPending(reservation);
        cancelPayment(reservation);
    }

    private void rejectIfNotAuthorized(Reservation reservation, Member member) {
        if (reservation.isNotOwner(member) && member.isNotAdmin()) {
            throw new AuthorizationException();
        }
    }

    private void updateFirstWaitingToPending(Reservation reservation) {
        reservationRepository.findNextWaiting(reservation.getDetail())
                .ifPresent(nextReservation -> {
                    nextReservation.toPending();
                    eventPublisher.publishTimeoutEvent(nextReservation);
                });
    }

    private void cancelPayment(Reservation reservation) {
        if (reservation.isPaid()) {
            Payment payment = reservation.getPayment();
            paymentClient.cancel(payment, CancelReason.empty());
        }
    }
}
