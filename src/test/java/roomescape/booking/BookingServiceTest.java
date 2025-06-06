package roomescape.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static roomescape.util.TestFactory.memberWithId;
import static roomescape.util.TestFactory.reservationTimeWithId;
import static roomescape.util.TestFactory.reservationWithId;
import static roomescape.util.TestFactory.scheduleWithId;
import static roomescape.util.TestFactory.themeWithId;
import static roomescape.util.TestFactory.waitingWithId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.booking.reservation.Reservation;
import roomescape.booking.reservation.ReservationPaymentStatus;
import roomescape.booking.reservation.ReservationService;
import roomescape.booking.waiting.Waiting;
import roomescape.booking.waiting.WaitingService;
import roomescape.member.Member;
import roomescape.member.MemberRole;
import roomescape.order.Order;
import roomescape.order.OrderRepository;
import roomescape.order.PaymentStatus;
import roomescape.reservationtime.ReservationTime;
import roomescape.schedule.Schedule;
import roomescape.theme.Theme;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private ReservationService reservationService;
    @Mock
    private WaitingService waitingService;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private BookingService bookingService;

    private Member member;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        ReservationTime reservationTime = reservationTimeWithId(1L, new ReservationTime(LocalTime.of(12, 40)));
        Theme theme = themeWithId(1L, new Theme("테마명", "테마 설명", "썸네일 URL"));
        schedule = scheduleWithId(1L, new Schedule(LocalDate.now().plusDays(1), reservationTime, theme));
        member = memberWithId(1L, new Member("email@example.com", "password", "사용자", MemberRole.MEMBER));
    }

    @Test
    @DisplayName("예약 삭제 시, 그 스케줄의 첫 번째 웨이팅을 예약으로 변경한다")
    void changeFirstWaitingToReservation() {
        // given
        Waiting firstWaiting = waitingWithId(1L, new Waiting(schedule, member, LocalDateTime.now()));
        Order order = new Order(UUID.randomUUID().toString(), 1000L, PaymentStatus.WAITING, member, schedule);
        Reservation reservation = reservationWithId(1L, new Reservation(member, schedule, order));
        given(reservationService.getById(1L)).willReturn(reservation);
        given(waitingService.existsBySchedule(schedule)).willReturn(true);
        given(waitingService.findFirstWaitingOfSchedule(schedule)).willReturn(firstWaiting);
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        bookingService.deleteReservationById(1L);

        // then
        then(reservationService).should().create(new Reservation(
                firstWaiting.getMember(),
                firstWaiting.getSchedule(),
                order,
                ReservationPaymentStatus.WAITING));
        then(reservationService).should().create(new Reservation(
                firstWaiting.getMember(),
                firstWaiting.getSchedule(),
                order, ReservationPaymentStatus.WAITING));
    }
}
