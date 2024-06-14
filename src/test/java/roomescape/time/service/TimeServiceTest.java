package roomescape.time.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;
import static roomescape.fixture.TimeFixture.TIME_2;
import static roomescape.fixture.TimeFixture.TIME_3;

import io.restassured.RestAssured;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.dto.AvailableTimeResponse;
import roomescape.time.dto.TimeCreateRequest;
import roomescape.time.dto.TimeResponse;
import roomescape.time.repository.TimeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TimeServiceTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TimeService timeService;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        ReservationTime time1 = timeRepository.save(TIME_1);
        ReservationTime time2 = timeRepository.save(TIME_2);
        ReservationTime time3 = timeRepository.save(TIME_3);
        Theme theme = themeRepository.save(THEME_1);

        reservationRepository.save(new Reservation(MEMBER_BRI, LocalDate.now().plusDays(1L), time1, theme,
                ReservationStatus.RESERVED));
        reservationRepository.save(new Reservation(MEMBER_BRI, LocalDate.now().plusDays(1L), time3, theme,
                ReservationStatus.RESERVED));
    }

    @DisplayName("예약 시간을 모두 조회할 수 있다.")
    @Test
    void findTimesTest() {
        assertThat(timeService.findTimes())
                .containsExactlyInAnyOrder(
                        new TimeResponse(1L, TIME_1.getStartAt()),
                        new TimeResponse(2L, TIME_2.getStartAt()),
                        new TimeResponse(3L, TIME_3.getStartAt())
                );
    }

    @DisplayName("예약 시간의 예약 가능 여부를 조회할 수 있다.")
    @Test
    void findAvailableTimesTest() {
        List<AvailableTimeResponse> expected = List.of(
                new AvailableTimeResponse(new TimeResponse(1L, LocalTime.of(19, 0)), true),
                new AvailableTimeResponse(new TimeResponse(2L, LocalTime.of(10, 0)), false));

        List<AvailableTimeResponse> actual = timeService.findAvailableTimes(LocalDate.now().plusDays(1L), 1L);

        assertThat(timeService.findAvailableTimes(LocalDate.now().plusDays(1L), 1L))
                .containsExactly(
                        new AvailableTimeResponse(new TimeResponse(1L, TIME_1.getStartAt()), true),
                        new AvailableTimeResponse(new TimeResponse(2L, TIME_2.getStartAt()), false),
                        new AvailableTimeResponse(new TimeResponse(3L, TIME_3.getStartAt()), true));
    }

    @DisplayName("예약 시간을 생성할 수 있다.")
    @Test
    void createTimeTest() {
        TimeCreateRequest request = new TimeCreateRequest(LocalTime.of(23, 0));
        TimeResponse expected = new TimeResponse(4L, LocalTime.of(23, 0));

        assertThat(timeService.createTime(request)).isEqualTo(expected);
    }

    @DisplayName("예약 시간 생성 시, 시간이 중복된다면 예외를 던진다.")
    @Test
    void createTimeTest_whenExistsStartAt() {
        TimeCreateRequest request = new TimeCreateRequest(TIME_1.getStartAt());

        assertThatThrownBy(() -> timeService.createTime(request))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("예약 시간은 중복될 수 없습니다.");
    }
}
