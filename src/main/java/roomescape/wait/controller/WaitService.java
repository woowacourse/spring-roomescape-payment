package roomescape.wait.controller;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberQueryService;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.schedule.domain.ReservationDate;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.schedule.service.ScheduleQueryService;
import roomescape.wait.controller.dto.CreateReservationWaitRequest;
import roomescape.wait.controller.dto.MyReservationWaitResponse;
import roomescape.wait.controller.dto.ReservationWaitResponse;
import roomescape.wait.domain.ReservationWait;
import roomescape.wait.service.ReservationWaitCommandService;
import roomescape.wait.service.ReservationWaitQueryService;

@Service
public class WaitService {
    private final ReservationWaitCommandService reservationWaitCommandService;
    private final ReservationWaitQueryService reservationWaitQueryService;
    private final ScheduleQueryService scheduleQueryService;
    private final MemberQueryService memberQueryService;

    public WaitService(
            final ReservationWaitCommandService reservationWaitCommandService,
            final ReservationWaitQueryService reservationWaitQueryService,
            final ScheduleQueryService scheduleQueryService,
            final MemberQueryService memberQueryService
    ) {
        this.reservationWaitCommandService = reservationWaitCommandService;
        this.reservationWaitQueryService = reservationWaitQueryService;
        this.scheduleQueryService = scheduleQueryService;
        this.memberQueryService = memberQueryService;
    }

    public ReservationWaitResponse createReservationWait(
            final CreateReservationWaitRequest request,
            final Long memberId
    ) {
        ReservationDate date = new ReservationDate(request.date());
        ReservationSchedule schedule = scheduleQueryService.getSchedule(request.time(), request.theme(), date);
        Member member = memberQueryService.getById(memberId);
        ReservationWait wait = reservationWaitCommandService.createReservationWait(schedule, member);
        return ReservationWaitResponse.from(wait);
    }

    public ReservationResponse approveReservationWait(final Long waitId) {
        ReservationWait wait = reservationWaitQueryService.getById(waitId);
        Reservation reservation = reservationWaitCommandService.approveReservationWait(wait);
        return ReservationResponse.from(reservation);
    }

    public void deleteReservationWait(final Long waitId, final Long memberId) {
        ReservationWait wait = reservationWaitQueryService.getById(waitId);
        Member member = memberQueryService.getById(memberId);
        reservationWaitCommandService.deleteReservationWait(wait, member);

    }

    public List<ReservationWaitResponse> getAllWaitReservation() {
        List<ReservationWait> allWaitReservation = reservationWaitQueryService.findAll();
        return ReservationWaitResponse.from(allWaitReservation);
    }

    public List<MyReservationWaitResponse> findAllMyWaitReservation(final Long memberId) {
        List<ReservationWait> myWaitReservations = reservationWaitQueryService.findAllMyWaitReservation(memberId);
        return MyReservationWaitResponse.from(myWaitReservations);
    }
}
