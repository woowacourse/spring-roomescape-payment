package roomescape.booking.reservation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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
import roomescape.payment.Payment;
import roomescape.payment.PaymentClient;
import roomescape.payment.PaymentRepository;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.schedule.Schedule;
import roomescape.schedule.ScheduleService;

@Service
@AllArgsConstructor
public class ReservationCreateService {

    private final ReservationRepository reservationRepository;
    private final ScheduleService scheduleService;
    private final MemberService memberService;
    private final OrderReader orderReader;
    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public ReservationResponse create(final ReservationPaymentRequest request, final LoginMember loginMember) {
        final Order order = orderReader.getById(request.orderId());
        final Member member = memberService.getByEmail(loginMember.email());
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(),
                request.themeId());
        validatePast(schedule);
        validateDuplication(schedule);

        order.pay(request.amount(), member, schedule);
        PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest(request.orderId(), request.amount(),
                request.paymentKey());
        paymentClient.confirm(paymentRequest);
        appendPayment(request.paymentKey(), order);

        final Reservation savedReservation = saveReservation(schedule, member, order);
        return ReservationResponse.from(savedReservation);
    }

    private Reservation saveReservation(final Schedule schedule, final Member member, Order order) {
        final Reservation notSavedReservation = new Reservation(
                member,
                schedule,
                order,
                ReservationPaymentStatus.SUCCESS
        );
        return reservationRepository.save(notSavedReservation);
    }

    private void validatePast(final Schedule schedule) {
        if (schedule.isPast()) {
            throw new ReservationPastDateException();
        }
    }

    private void validateDuplication(final Schedule schedule) {
        if (reservationRepository.existsBySchedule(schedule)) {
            throw new ReservationConflictException();
        }
    }

    private void appendPayment(String paymentKey, Order order) {
        Payment payment = Payment.create(order.getAmount(), paymentKey, order);
        paymentRepository.save(payment);
    }

    @Transactional
    public ReservationResponse createForAdmin(final AdminReservationRequest request) {
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(),
                request.themeId());
        validatePast(schedule);
        validateDuplication(schedule);

        final Member member = memberService.getById(request.memberId());
        final Reservation savedReservation = saveReservation(schedule, member, null);
        return ReservationResponse.from(savedReservation);
    }
}
