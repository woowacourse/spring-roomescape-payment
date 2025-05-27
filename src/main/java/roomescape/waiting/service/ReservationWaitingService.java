package roomescape.waiting.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.service.ReservationThemeService;
import roomescape.time.service.ReservationTimeService;
import roomescape.waiting.domain.ReservationWaiting;
import roomescape.waiting.dto.AdminReservationWaitingResponse;
import roomescape.waiting.dto.ReservationWaitingRequest;
import roomescape.waiting.dto.ReservationWaitingResponse;
import roomescape.waiting.repository.ReservationWaitingRepository;

@Service
@RequiredArgsConstructor
public class ReservationWaitingService {

    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationRepository reservationRepository;
    private final MemberService memberService;
    private final ReservationTimeService reservationTimeService;
    private final ReservationThemeService reservationThemeService;

    public ReservationWaitingResponse addReservationWaiting(final ReservationWaitingRequest request,
                                                            final long memberId) {
        final Member member = memberService.getMemberById(memberId);
        validateDuplicateWaiting(request, memberId);
        validateExistsReservation(request, memberId);
        final ReservationWaiting reservation = new ReservationWaiting(member, request.date(),
                reservationTimeService.getById(request.timeId()), reservationThemeService.getById(request.themeId()));
        return ReservationWaitingResponse.from(reservationWaitingRepository.save(reservation));
    }

    public void removeReservationWaiting(final long id) {
        validateReservationWaiting(id);
        reservationWaitingRepository.deleteById(id);
    }

    private void validateReservationWaiting(final long id) {
        if (!reservationWaitingRepository.existsById(id)) {
            throw new NoSuchElementException("[ERROR] 존재하지 않는 예약대기 입니다.");
        }
    }

    public List<AdminReservationWaitingResponse> getAllReservationWaiting() {
        final List<ReservationWaiting> reservationWaitings = reservationWaitingRepository.findAll();
        return reservationWaitings.stream()
                .map(AdminReservationWaitingResponse::from)
                .toList();
    }

    private void validateDuplicateWaiting(final ReservationWaitingRequest request, final long memberId) {
        if (reservationWaitingRepository.existsByMemberIdAndThemeIdAndTimeIdAndDate(
                memberId,
                request.themeId(),
                request.timeId(),
                request.date()
        )) {
            throw new IllegalArgumentException("[ERROR] 중복된 예약 대기 입니다.");
        }
    }

    private void validateExistsReservation(final ReservationWaitingRequest request, final long memberId) {
        if (reservationRepository.existsByMemberIdAndDateAndThemeIdAndTimeId(memberId, request.date(),
                request.themeId(), request.timeId())) {
            throw new IllegalArgumentException("[ERROR] 이미 예약이 존재합니다.");
        }

    }
}
