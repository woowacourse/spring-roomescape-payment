package roomescape.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.RegistrationSlot;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.WaitingReservationRequest;
import roomescape.reservation.dto.WaitingReservationResponse;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Service
@RequiredArgsConstructor
public class WaitingReservationService {

    private final WaitingReservationRepository waitingReservationRepository;

    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public WaitingReservationResponse registerWaitingReservation(final WaitingReservationRequest request, final LoginMember loginMember) {
        final Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 멤버입니다."));
        final ReservationTime time = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 시간입니다."));
        final Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다."));

        final WaitingReservation waitingReservation = WaitingReservation.builder()
                .member(member)
                .registrationSlot(new RegistrationSlot(time, theme, request.date()))
                .build();
        return new WaitingReservationResponse(waitingReservationRepository.save(waitingReservation));
    }

    @Transactional
    public void denyWaitingByIdForAdmin(Long waitingId) {
        WaitingReservation waiting = getWaitingById(waitingId);
        waitingReservationRepository.delete(waiting);
    }

    @Transactional
    public void cancelWaitingByIdForMember(Long waitingId, LoginMember loginMember) {
        WaitingReservation waiting = getWaitingById(waitingId);
        validateCancelPermission(loginMember, waiting);

        waitingReservationRepository.delete(waiting);
    }

    private void validateCancelPermission(LoginMember loginMember, WaitingReservation waiting) {
        boolean notSameMember = !waiting.isOwnedBy(loginMember.id());
        if (notSameMember) {
            throw new ReservationException("자신의 예약만 삭제할 수 있습니다.");
        }
    }

    private WaitingReservation getWaitingById(Long waitingId) {
        return waitingReservationRepository.findById(waitingId)
                .orElseThrow(() -> new NotFoundException("Waiting을 찾지 못했습니다, waitingId: " + waitingId));
    }
}
