package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.fixture.MemberFixture.MEMBER_SUN;
import static roomescape.fixture.ThemeFixture.THEME_BED;
import static roomescape.fixture.TimeFixture.ONE_PM;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.request.time.ReservationTimeRequest;
import roomescape.application.dto.response.time.ReservationTimeResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.exception.time.DuplicatedTimeException;
import roomescape.exception.time.ReservationReferencedTimeException;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ReservationTimeServiceTest {

    @Autowired
    ReservationTimeService timeService;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationRepository reservationRepository;

    Reservation reservation(Member member, Theme theme, String date, ReservationTime time, Status status) {
        return new Reservation(member, theme, LocalDate.parse(date), time, status);
    }

    @DisplayName("중복된 예약 시간을 저장하려고 시도하면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_save_duplicated_time() {
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.parse(ONE_PM.getStartAt()));

        assertThatThrownBy(() -> timeService.saveReservationTime(request))
                .isInstanceOf(DuplicatedTimeException.class);
    }

    @DisplayName("예약 시간을 정상적으로 저장한다.")
    @Test
    void success_save_reservation_time() {
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.parse(ONE_PM.getStartAt()));

        ReservationTimeResponse response = timeService.saveReservationTime(request);

        assertThat(response.startAt()).isEqualTo(request.startAt());
    }

    @DisplayName("예약이 존재하는 예약 시간을 삭제하려고 시도하면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_delete_exists_reservation_time() {
        Member sun = memberRepository.save(MEMBER_SUN.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        reservationRepository.save(reservation(sun, bed, "2024-06-01", onePm, RESERVED));

        assertThatThrownBy(() -> timeService.deleteReservationTime(1L))
                .isInstanceOf(ReservationReferencedTimeException.class);
    }

    @DisplayName("예약 시간을 정상적으로 삭제한다.")
    @Test
    void success_delete_reservation_time() {
        ReservationTime onePm = timeRepository.save(ONE_PM.create());

        assertThatNoException()
                .isThrownBy(() -> timeService.deleteReservationTime(1L));
    }
}
