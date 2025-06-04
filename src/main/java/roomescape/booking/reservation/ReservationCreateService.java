package roomescape.booking.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.reservation.dto.AdminReservationRequest;
import roomescape.booking.reservation.dto.ReservationRequest;
import roomescape.booking.reservation.dto.ReservationResponse;
import roomescape.exception.custom.reason.reservation.ReservationConflictException;
import roomescape.exception.custom.reason.reservation.ReservationPastDateException;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.order.Order;
import roomescape.order.OrderReader;
import roomescape.reservationpayment.ReservationPaymentService;
import roomescape.reservationpayment.dto.ReservationPaymentRequest;
import roomescape.schedule.Schedule;
import roomescape.schedule.ScheduleService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationCreateService {

    private final ReservationRepository reservationRepository;
    private final ScheduleService scheduleService;
    private final MemberService memberService;
    private final OrderReader orderReader;
    private final ReservationPaymentService reservationPaymentService;

    @Transactional
    public ReservationResponse create(final ReservationRequest request, final LoginMember loginMember) {
        final Member member = memberService.getByEmail(loginMember.email());
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId());
        validateOrder(request, member, schedule);
        final Reservation reservation = saveReservation(schedule, member);
        ReservationResponse response = ReservationResponse.from(reservation);

        reservation.markStatusAsConfirmed();
        confirmPayment(request, reservation);

        return response;
    }

    private void validateOrder(final ReservationRequest request, final Member member, final Schedule schedule) {
        final Order order = orderReader.getById(request.orderId());
        order.validateOrderAndPaymentRequest(request.amount(), member, schedule);
    }

    private Reservation saveReservation(final Schedule schedule, final Member member) {
        validatePast(schedule);
        validateDuplication(schedule);
        final Reservation notSavedReservation = new Reservation(member, schedule, ReservationStatus.PENDING);
        return reservationRepository.save(notSavedReservation);
    }

    private void confirmPayment(final ReservationRequest request, final Reservation reservation) {
        ReservationPaymentRequest confirmRequest = new ReservationPaymentRequest(request.orderId(), request.amount(), request.paymentKey(), reservation);
        reservationPaymentService.confirmPayment(confirmRequest);
    }

    private void validatePast(final Schedule schedule) {
        if (schedule.isPast()) {
            throw new ReservationPastDateException();
        }
    }

    private void validateDuplication(final Schedule schedule) {
        if (reservationRepository.existsByScheduleAndReservationStatusNot(schedule, ReservationStatus.CANCELED)) {
            throw new ReservationConflictException();
        }
    }

    @Transactional
    public ReservationResponse createForAdmin(final AdminReservationRequest request) {
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId());
        validatePast(schedule);
        validateDuplication(schedule);

        final Member member = memberService.getById(request.memberId());
        final Reservation savedReservation = saveReservationForAdmin(schedule, member);
        return ReservationResponse.from(savedReservation);
    }

    private Reservation saveReservationForAdmin(final Schedule schedule, final Member member) {
        final Reservation notSavedReservation = new Reservation(member, schedule, ReservationStatus.PROMOTED);
        return reservationRepository.save(notSavedReservation);
    }
}
