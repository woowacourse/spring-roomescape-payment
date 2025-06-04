package roomescape.booking.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.reservation.dto.AdminReservationRequest;
import roomescape.booking.reservation.dto.ReservationPaymentRequest;
import roomescape.booking.reservation.dto.ReservationResponse;
import roomescape.booking.reservation.reservationpayment.ReservationPayment;
import roomescape.booking.reservation.reservationpayment.ReservationPaymentRepository;
import roomescape.exception.custom.reason.reservation.ReservationConflictException;
import roomescape.exception.custom.reason.reservation.ReservationPastDateException;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.order.Order;
import roomescape.order.OrderReader;
import roomescape.payment.TossPaymentAdapter;
import roomescape.payment.dto.TossPaymentConfirmCommand;
import roomescape.payment.dto.TossPaymentConfirmResponse;
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
    private final TossPaymentAdapter tossPaymentAdapter;
    private final TossPaymentConfirmCommandFactory tossPaymentConfirmCommandFactory;
    private final ReservationPaymentRepository reservationPaymentRepository;

    @Transactional
    public ReservationResponse create(final ReservationPaymentRequest request, final LoginMember loginMember) {
        final Member member = memberService.getByEmail(loginMember.email());
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId());
        validateOrder(request, member, schedule);
        final Reservation reservation = saveReservation(schedule, member);
        ReservationResponse response = ReservationResponse.from(reservation);

        reservation.markStatusAsConfirmed();
        confirmPayment(request, reservation);
        return response;
    }

    private void validateOrder(final ReservationPaymentRequest request, final Member member, final Schedule schedule) {
        final Order order = orderReader.getById(request.orderId());
        order.validateOrderAndPaymentRequest(request.amount(), member, schedule);
    }

    private Reservation saveReservation(final Schedule schedule, final Member member) {
        validatePast(schedule);
        validateDuplication(schedule);
        final Reservation notSavedReservation = new Reservation(member, schedule, ReservationStatus.PENDING);
        return reservationRepository.save(notSavedReservation);
    }

    private void confirmPayment(final ReservationPaymentRequest request, final Reservation reservation) {
        TossPaymentConfirmResponse response;
        try {
            TossPaymentConfirmCommand confirmCommand = tossPaymentConfirmCommandFactory.toPaymentConfirmCommand(request);
            response = tossPaymentAdapter.confirmPayment(confirmCommand);
        } catch (Exception e) {
            log.error("결제 승인 실패", e);
            throw e;
        }
        ReservationPayment reservationPayment = new ReservationPayment(response.paymentKey(), response.totalAmount(), response.orderId(), reservation);
        reservationPaymentRepository.save(reservationPayment);
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
