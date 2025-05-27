package roomescape.reservation.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberQueryService;
import roomescape.reservation.controller.dto.AdminCreateReservationRequest;
import roomescape.reservation.controller.dto.CreateReservationRequest;
import roomescape.reservation.controller.dto.MyReservationResponse;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.ReservationCommandService;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.schedule.domain.ReservationDate;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.schedule.service.ScheduleQueryService;

@Service
public class ReservationService {
    private final ReservationCommandService reservationCommandService;
    private final ScheduleQueryService scheduleQueryService;
    private final MemberQueryService memberQueryService;
    private final ReservationQueryService reservationQueryService;

    public ReservationService(
            final ReservationCommandService reservationCommandService,
            final ScheduleQueryService scheduleQueryService,
            final MemberQueryService memberQueryService,
            final ReservationQueryService reservationQueryService) {
        this.reservationCommandService = reservationCommandService;
        this.scheduleQueryService = scheduleQueryService;
        this.memberQueryService = memberQueryService;
        this.reservationQueryService = reservationQueryService;
    }

    public ReservationResponse createReservationByAdmin(final AdminCreateReservationRequest request) {
        return createReservation(request.timeId(), request.themeId(), request.date(), request.memberId());
    }

    public ReservationResponse createReservation(final CreateReservationRequest request, final Long memberId) {
        return createReservation(request.timeId(), request.themeId(), request.date(), memberId);
    }

    private ReservationResponse createReservation(Long timeId, Long themeId, LocalDate date, Long memberId) {
        ReservationDate reservationDate = new ReservationDate(date);
        ReservationSchedule schedule = scheduleQueryService.getSchedule(timeId, themeId, reservationDate);
        Member member = memberQueryService.getById(memberId);
        Reservation reservation = reservationCommandService.createReservation(schedule, member);
        return ReservationResponse.from(reservation);
    }

    public void deleteById(final Long id) {
        reservationCommandService.deleteReservationById(id);
    }

    public List<MyReservationResponse> findAllMyReservation(final Long memberId) {
        return MyReservationResponse.from(reservationQueryService.findAllMyReservation(memberId));
    }

    public List<ReservationResponse> findAllReservationsWithFilter(
            final Long memberId,
            final Long themeId,
            final LocalDate fromDate,
            final LocalDate toDate
    ) {
        List<Reservation> reservations = reservationQueryService.
                findAllReservationsWithFilter(memberId, themeId, fromDate, toDate);
        return ReservationResponse.from(reservations);
    }
}
