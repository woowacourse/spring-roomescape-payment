package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.custom.BadRequestException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.domain.*;
import roomescape.reservation.domain.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class WaitingReservationService {

    private final ReservationService reservationService;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    public WaitingReservationService(ReservationService reservationService, ReservationRepository reservationRepository,
                                     MemberRepository memberRepository) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
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
            int waitingCount = reservationRepository
                    .findMyWaitingOrder(reservationWithStatus.reservationId());
            return ReservationViewResponse.from(reservationWithStatus, waitingCount);
        }
        return ReservationViewResponse.from(reservationWithStatus);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByWaitingReservation() {
        List<Reservation> reservations = reservationRepository
                .findAllByStatus(ReservationStatus.WAITING);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void deleteReservation(AuthInfo authInfo, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 사용자 예약이 없습니다."));
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(() -> new BadRequestException("해당 유저를 찾을 수 없습니다."));
        if (!member.isAdmin() && !reservation.isBookedBy(member)) {
            throw new ForbiddenException("예약자가 아닙니다.");
        }
        reservationRepository.deleteById(reservationId);
        reservationRepository.findFirstByReservationSlotOrderByCreatedAt(reservation.getReservationSlot())
                        .ifPresent(Reservation::pendingReservation);
    }

    @Transactional
    public ReservationResponse reserveWaiting(ReservationRequest reservationRequest, Long memberId) {
        return reservationService.createReservation(reservationRequest, memberId, ReservationStatus.WAITING);
    }

    @Transactional
    public ReservationResponse confirmReservation(WaitingReservationPaymentRequest waitingReservationPaymentRequest, Long memberId) {
        return reservationService.confirmReservation(waitingReservationPaymentRequest, memberId);
    }
}
