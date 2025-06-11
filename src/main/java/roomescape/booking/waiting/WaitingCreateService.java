package roomescape.booking.waiting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.reservation.ReservationService;
import roomescape.booking.waiting.dto.WaitingRequest;
import roomescape.booking.waiting.dto.WaitingResponse;
import roomescape.exception.custom.reason.reservation.ReservationNotExistsScheduleException;
import roomescape.exception.custom.reason.waiting.WaitingPastScheduleException;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.schedule.Schedule;
import roomescape.schedule.ScheduleService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingCreateService {

    private final WaitingRepository waitingRepository;
    private final ReservationService reservationService;
    private final ScheduleService scheduleService;
    private final MemberService memberService;

    @Transactional
    public WaitingResponse create(final WaitingRequest request, final LoginMember loginMember) {
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId());
        validatePast(schedule);
        validateExistsReservationAboutSchedule(schedule);

        Waiting savedWaiting = saveWaiting(loginMember, schedule);
        return WaitingResponse.of(savedWaiting);
    }

    private Waiting saveWaiting(final LoginMember loginMember, final Schedule schedule) {
        final Member member = memberService.getByEmail(loginMember.email());
        final Waiting waiting = new Waiting(schedule, member, LocalDateTime.now());
        Waiting savedWaiting = waitingRepository.save(waiting);
        log.info("EVENT: WAITING_CREATED, id={}, memberId={}, themeId={}, date={}, time={}",
                savedWaiting.getId(),
                savedWaiting.getMember().getId(),
                savedWaiting.getSchedule().getId(),
                savedWaiting.getSchedule().getDate(),
                savedWaiting.getSchedule().getReservationTime().getStartAt());
        return savedWaiting;
    }

    private void validatePast(final Schedule schedule) {
        if (schedule.isPast()) {
            throw new WaitingPastScheduleException(schedule.getDate(), schedule.getReservationTime().getStartAt());
        }
    }

    private void validateExistsReservationAboutSchedule(final Schedule schedule) {
        boolean isReservationExist = reservationService.existsBySchedule(schedule);
        if (!isReservationExist) {
            throw new ReservationNotExistsScheduleException(schedule.getDate(), schedule.getReservationTime().getStartAt());
        }
    }
}
