package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.custom.BadRequestException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.member.service.MemberService;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.domain.*;
import roomescape.reservation.domain.repository.ReservationRepository;

import java.util.List;

@Service
public class WaitingReservationService {

    private final ReservationRegister reservationRegister;
    private final MemberService memberService;
    private final ReservationService reservationService;

    public WaitingReservationService(ReservationRegister reservationRegister, ReservationService reservationService,
                                     MemberService memberService) {
        this.reservationRegister = reservationRegister;
        this.reservationService = reservationService;
        this.memberService = memberService;
    }

    public List<ReservationViewResponse> convertReservationsWithStatusToViewResponses(
            List<ReservationWithStatus> reservationWithStatuses
    ) {
        return reservationWithStatuses
                .stream()
                .map(this::generateReservationViewResponse)
                .toList();
    }

    private ReservationViewResponse generateReservationViewResponse(ReservationWithStatus reservationWithStatus) {
        if (reservationWithStatus.status().isWaiting()) {
            int waitingCount = reservationService
                    .findMyWaitingOrder(reservationWithStatus.reservationId());
            return ReservationViewResponse.from(reservationWithStatus, waitingCount);
        }
        return ReservationViewResponse.from(reservationWithStatus);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByWaitingReservation() {
        List<Reservation> reservations = reservationService
                .findReservations(ReservationStatus.WAITING);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void deleteReservation(AuthInfo authInfo, Long reservationId) {
        Reservation reservation = reservationService.findReservation(reservationId);
        Member member = memberService.findMember(authInfo.getId());
        if (!member.isAdmin() && !reservation.isBookedBy(member)) {
            throw new ForbiddenException("예약자가 아닙니다.");
        }
        reservationService.deleteReservation(reservationId);
        reservationService.updateWaitingOrder(reservation.getReservationSlot());
    }

    @Transactional
    public ReservationResponse reserveWaiting(ReservationRequest reservationRequest, Long memberId) {
        return reservationRegister.createReservation(reservationRequest, memberId, ReservationStatus.WAITING);
    }

    @Transactional
    public ReservationResponse confirmReservation(WaitingReservationPaymentRequest waitingReservationPaymentRequest, Long memberId) {
        return reservationRegister.confirmReservation(waitingReservationPaymentRequest, memberId);
    }
}
