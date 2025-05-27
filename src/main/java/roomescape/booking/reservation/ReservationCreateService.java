package roomescape.booking.reservation;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.reservation.dto.AdminReservationRequest;
import roomescape.booking.reservation.dto.ReservationPaymentRequest;
import roomescape.booking.reservation.dto.ReservationRequest;
import roomescape.booking.reservation.dto.ReservationResponse;
import roomescape.exception.custom.reason.reservation.ReservationConflictException;
import roomescape.exception.custom.reason.reservation.ReservationPastDateException;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.order.Order;
import roomescape.order.OrderReader;
import roomescape.schedule.Schedule;
import roomescape.schedule.ScheduleService;

import java.util.Base64;

@Service
@AllArgsConstructor
public class ReservationCreateService {

    private final ReservationRepository reservationRepository;
    private final ScheduleService scheduleService;
    private final MemberService memberService;
    private final OrderReader orderReader;
    private final RestClient restClient;

    @Transactional
    public ReservationResponse create(final ReservationRequest request, final LoginMember loginMember) {
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId());
        validatePast(schedule);
        validateDuplication(schedule);

        final Member member = memberService.getByEmail(loginMember.email());
        final Reservation savedReservation = saveReservation(schedule, member);
        return ReservationResponse.from(savedReservation);
    }

    private Reservation saveReservation(final Schedule schedule, final Member member) {
        final Reservation notSavedReservation = new Reservation(member, schedule);
        final Reservation savedReservation = reservationRepository.save(notSavedReservation);
        return savedReservation;
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

    @Transactional
    public ReservationResponse createV2(final ReservationPaymentRequest request, final LoginMember loginMember) {
        final Order order = orderReader.getById(request.orderId());
        final Member member = memberService.getByEmail(loginMember.email());
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId());
        order.pay(request.amount(), member, schedule);

        String testWidgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
        String encodedKey = Base64.getEncoder().encodeToString(testWidgetSecretKey.getBytes());

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header("Authorization", "Basic " + encodedKey)
                    .body(new PaymentRequests(request.orderId(), request.amount(), request.paymentKey()))
                    .retrieve()
                    .toBodilessEntity();
            System.out.println(response.getStatusCode());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        validatePast(schedule);
        validateDuplication(schedule);
        final Reservation savedReservation = saveReservation(schedule, member);
        return ReservationResponse.from(savedReservation);
    }

    @Transactional
    public ReservationResponse createForAdmin(final AdminReservationRequest request) {
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId());
        validatePast(schedule);
        validateDuplication(schedule);

        final Member member = memberService.getById(request.memberId());
        final Reservation savedReservation = saveReservation(schedule, member);
        return ReservationResponse.from(savedReservation);
    }

    private record PaymentRequests(
            String orderId,
            Long amount,
            String paymentKey
    ) {

    }
}
