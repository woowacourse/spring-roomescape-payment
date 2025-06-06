package roomescape.reservation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.common.event.EventPublisher;
import roomescape.exception.NotFoundException;
import roomescape.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.RegistrationSlot;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.domain.WaitingValidator;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.WaitingReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservation.service.dto.CreateRegistrationCommand;
import roomescape.reservation.service.dto.WaitingApprovedEvent;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingReservationService {

    private final WaitingValidator waitingValidator;
    private final EventPublisher eventPublisher;
    private final WaitingReservationRepository waitingReservationRepository;

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public List<ReservationResponse> getAll() {
        final List<WaitingReservation> waitingReservations = waitingReservationRepository.findAll();
        return ReservationResponse.fromWaitings(waitingReservations);
    }

    @Transactional
    public WaitingReservationResponse registerWaitingReservation(CreateRegistrationCommand command) {
        final Member member = getMemberById(command.memberId());
        final ReservationTime time = getTimeById(command.timeId());
        final Theme theme = getThemeById(command.themeId());
        final WaitingReservation waitingReservation = WaitingReservation.builder()
                .member(member)
                .registrationSlot(new RegistrationSlot(time, theme, command.date()))
                .build();
        waitingValidator.validateCanWaiting(waitingReservation);
        WaitingReservation saved = waitingReservationRepository.save(waitingReservation);

        log.info("대기 예약 등록 완료 - waitingId={}", saved.getId());
        return new WaitingReservationResponse(saved);
    }

    @Transactional
    public void approveWaitingReservation(final Long id) {
        final WaitingReservation waitingReservation = getWaitingById(id);

        waitingValidator.validateCanWaitingApprove(waitingReservation);
        Reservation approvedReservation = waitingReservation.approveToReservation();
        reservationRepository.save(approvedReservation);
        eventPublisher.raise(new WaitingApprovedEvent(approvedReservation));

        waitingReservationRepository.deleteById(waitingReservation.getId());
        log.info("대기 승인 완료 - reservationId={}, waitingId={}",
                approvedReservation.getId(), waitingReservation.getId());
    }

    @Transactional
    public void denyWaitingByIdForAdmin(Long waitingId) {
        WaitingReservation waiting = getWaitingById(waitingId);
        waitingReservationRepository.delete(waiting);
        log.info("관리자 거절 완료 - waitingId={}", waitingId);
    }

    @Transactional
    public void cancelWaitingByIdForMember(Long waitingId, LoginMember loginMember) {
        WaitingReservation waiting = getWaitingById(waitingId);
        validateCancelPermission(loginMember, waiting);

        waitingReservationRepository.delete(waiting);
        log.info("회원 요청에 의한 대기 취소 완료 - waitingId={}, memberId={}",
                waitingId, loginMember.id());
    }

    private void validateCancelPermission(LoginMember loginMember, WaitingReservation waiting) {
        boolean notSameMember = !waiting.isOwnedBy(loginMember.id());
        if (notSameMember) {
            throw new ReservationException("자신의 예약만 삭제할 수 있습니다.");
        }
    }

    private Theme getThemeById(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다."));
    }

    private ReservationTime getTimeById(Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 시간입니다."));
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 멤버입니다."));
    }

    private WaitingReservation getWaitingById(Long waitingId) {
        return waitingReservationRepository.findById(waitingId)
                .orElseThrow(() -> new NotFoundException("Waiting을 찾지 못했습니다, waitingId: " + waitingId));
    }
}
