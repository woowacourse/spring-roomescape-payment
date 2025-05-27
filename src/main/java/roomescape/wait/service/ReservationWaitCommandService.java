package roomescape.wait.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.AccessDeniedException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.ReservationCommandService;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.wait.domain.ReservationWait;
import roomescape.wait.repository.ReservationWaitRepository;

@Service
@Transactional
public class ReservationWaitCommandService {
    private final ReservationWaitRepository reservationWaitRepository;
    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;

    public ReservationWaitCommandService(
            final ReservationWaitRepository reservationWaitRepository,
            final ReservationQueryService reservationQueryService,
            final ReservationCommandService reservationCommandService
    ) {
        this.reservationWaitRepository = reservationWaitRepository;
        this.reservationQueryService = reservationQueryService;
        this.reservationCommandService = reservationCommandService;
    }

    @Transactional
    public ReservationWait createReservationWait(
            final ReservationSchedule schedule,
            final Member member
    ) {
        if (!reservationQueryService.existsReservation(schedule)) {
            throw new IllegalStateException("해당 일정에 예약이 없어서 예약 대기가 불가능합니다.");
        }
        return reservationWaitRepository.save(new ReservationWait(null, member, schedule));
    }

    @Transactional
    public Reservation approveReservationWait(final ReservationWait wait) {
        ReservationSchedule schedule = wait.getSchedule();
        if (reservationQueryService.existsReservation(schedule)) {
            throw new IllegalStateException("예약 대기를 승인하려면 해당 예약 일정에 예약이 없어야 합니다.");
        }
        Reservation savedReservation = reservationCommandService.createReservation(schedule, wait.getMember());
        reservationWaitRepository.deleteById(wait.getId());
        return savedReservation;
    }

    public void deleteReservationWait(
            ReservationWait wait,
            Member member
    ) {
        if (member.getRole() != MemberRole.ADMIN && !wait.getMember().equals(member)) {
            throw new AccessDeniedException("예약 대기는 어드민 또는 본인만 취소 가능합니다.");
        }
        reservationWaitRepository.deleteById(wait.getId());
    }
}
