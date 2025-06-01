package roomescape.booking.reservation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.reservation.dto.AdminReservationRequest;
import roomescape.booking.reservation.dto.ReservationPaymentRequest;
import roomescape.booking.reservation.dto.ReservationResponse;
import roomescape.exception.custom.reason.reservation.ReservationConflictException;
import roomescape.exception.custom.reason.reservation.ReservationPastDateException;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.order.Order;
import roomescape.order.OrderReader;
import roomescape.payment.TossPaymentAdapter;
import roomescape.payment.dto.TossPaymentConfirmCommand;
import roomescape.schedule.Schedule;
import roomescape.schedule.ScheduleService;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationCreateService {

    private final ReservationRepository reservationRepository;
    private final ScheduleService scheduleService;
    private final MemberService memberService;
    private final OrderReader orderReader;
    private final TossPaymentAdapter tossPaymentAdapter;

    @Transactional
    public ReservationResponse create(final ReservationPaymentRequest request, final LoginMember loginMember) {
        final Member member = memberService.getByEmail(loginMember.email());
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId());
        final Order order = getOrder(request, member, schedule);
        final Reservation reservation = saveReservation(schedule, member, request.orderId());
        ReservationResponse response = ReservationResponse.from(reservation);

        try {
            TossPaymentConfirmCommand confirmCommand = new TossPaymentConfirmCommand(request.orderId(), request.amount(), request.paymentKey());
            tossPaymentAdapter.confirmPayment(confirmCommand);
        } catch (Exception e) {
            reservation.isCanceled();
            throw e;
        }

        try {
            confirmReservation(order, reservation);
        } catch (Exception e) {
            log.error("결제 상태 업데이트 실패", e);
        }
        return response;
    }

    private Order getOrder(final ReservationPaymentRequest request, final Member member, final Schedule schedule) {
        final Order order = orderReader.getById(request.orderId());
        order.validateOrder(request.amount(), member, schedule);
        order.updatePaymentKey(request.paymentKey());
        return order;
    }

    private Reservation saveReservation(final Schedule schedule, final Member member, final String orderId) {
        validatePast(schedule);
        validateDuplication(schedule);
        final Reservation notSavedReservation = new Reservation(member, schedule, ReservationStatus.PENDING, orderId);
        return reservationRepository.save(notSavedReservation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void confirmReservation(final Order order, final Reservation reservation) {
        order.isPaidStatus();
        reservation.isConfirmed();
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
