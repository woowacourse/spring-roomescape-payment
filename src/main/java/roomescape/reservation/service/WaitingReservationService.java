package roomescape.reservation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.RegistrationSlot;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.WaitingReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservation.service.dto.CreateRegistrationCommand;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Service
@RequiredArgsConstructor
public class WaitingReservationService {

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
        return new WaitingReservationResponse(waitingReservationRepository.save(waitingReservation));
    }

    @Transactional
    public void approveWaitingReservation(final Long id) {
        final WaitingReservation waitingReservation = getWaitingById(id);

        validateCanWaiting(waitingReservation);
        Reservation approvedReservation = waitingReservation.approveToReservation();

        waitingReservationRepository.deleteById(waitingReservation.getId());
        reservationRepository.save(approvedReservation);
    }

    private void validateCanWaiting(WaitingReservation waitingReservation) {
        if (existsAlreadyInSlot(waitingReservation)) {
            throw new ReservationException("이미 해당 날짜에 예약이 존재합니다.");
        }
    }

    private boolean existsAlreadyInSlot(WaitingReservation waitingReservation) {
        return reservationRepository.existsSameSlot(
                waitingReservation.getDate(),
                waitingReservation.getTime().getId(),
                waitingReservation.getTheme().getId()
        );
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
