package roomescape.reservation.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberQueryService;
import roomescape.reservation.controller.dto.AdminCreateReservationRequest;
import roomescape.reservation.controller.dto.CreateReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.domain.PaymentType;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.dto.MyReservationWithTossPayment;
import roomescape.reservation.service.PaymentService;
import roomescape.reservation.service.PaymentServiceDecider;
import roomescape.reservation.service.PaymentServiceDeciderImpl;
import roomescape.reservation.service.ReservationCommandService;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.reservation.service.dto.PaymentRequest;
import roomescape.schedule.domain.ReservationDate;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.schedule.service.ScheduleQueryService;

@Service
public class ReservationService {
    private final ReservationCommandService reservationCommandService;
    private final ScheduleQueryService scheduleQueryService;
    private final MemberQueryService memberQueryService;
    private final ReservationQueryService reservationQueryService;
    private final PaymentServiceDecider paymentServiceDecider;

    public ReservationService(
            final ReservationCommandService reservationCommandService,
            final ScheduleQueryService scheduleQueryService,
            final MemberQueryService memberQueryService,
            final ReservationQueryService reservationQueryService,
            final PaymentServiceDecider paymentServiceDecider) {
        this.reservationCommandService = reservationCommandService;
        this.scheduleQueryService = scheduleQueryService;
        this.memberQueryService = memberQueryService;
        this.reservationQueryService = reservationQueryService;
        this.paymentServiceDecider = paymentServiceDecider;
    }

    public ReservationResponse createReservationByAdmin(final AdminCreateReservationRequest request) {
        return createReservation(request.timeId(), request.themeId(), request.date(), request.memberId());
    }

    @Transactional
    public ReservationResponse createReservationWithPayment(
            final CreateReservationRequest request,
            final PaymentRequest paymentRequest,
            final Long memberId
    ) {
        ReservationSchedule schedule = scheduleQueryService.getSchedule(
                request.timeId(),
                request.themeId(),
                new ReservationDate(request.date())
        );
        Member member = memberQueryService.getById(memberId);
        Reservation reservation = reservationCommandService.createReservation(schedule, member);
        PaymentService<PaymentRequest> paymentService = paymentServiceDecider.decide(PaymentType.TOSS); // PG사 여러개라면 타입 받아와야
        paymentService.createPayment(paymentRequest, reservation);
        return ReservationResponse.from(reservation);
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

    public List<MyReservationWithTossPayment> findAllMyReservation(final Long memberId) {
        return reservationQueryService.findAllMyReservationWithTossPayment(memberId);
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
