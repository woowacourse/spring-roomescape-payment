package roomescape.booking;

import jakarta.transaction.Transactional;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import roomescape.booking.reservation.Reservation;
import roomescape.booking.reservation.ReservationPaymentStatus;
import roomescape.booking.reservation.ReservationRepository;
import roomescape.booking.waiting.Waiting;
import roomescape.booking.waiting.WaitingRepository;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.member.MemberRole;
import roomescape.reservationtime.ReservationTime;
import roomescape.reservationtime.ReservationTimeRepository;
import roomescape.schedule.Schedule;
import roomescape.schedule.ScheduleRepository;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ReservationRepository reservationRepository;

    private Member member;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 40)));
        Theme theme = themeRepository.save(new Theme("테마명", "테마 설명", "썸네일 URL"));
        schedule = scheduleRepository.save(new Schedule(LocalDate.now().plusDays(1), reservationTime, theme));
        member = memberRepository.save(new Member("email@example.com", "password", "사용자", MemberRole.MEMBER));
    }

    @Test
    @DisplayName("예약 삭제 시, 그 스케줄의 첫 번째 웨이팅을 예약으로 변경한다")
    void changeFirstWaitingToReservation() {
        // given
        waitingRepository.save(new Waiting(schedule, member, LocalDateTime.now()));
        Reservation reservation = reservationRepository.save(new Reservation(member, schedule, ReservationPaymentStatus.SUCCESS));

        // whenR
        bookingService.deleteReservationById(reservation.getId());

        // then
        assertThat(waitingRepository.findAll()).hasSize(0);
        assertThat(reservationRepository.findAll()).hasSize(1)
                .extracting("member", "schedule", "paymentStatus")
                .containsExactly(Tuple.tuple(member, schedule, ReservationPaymentStatus.WAITING));
    }
}
