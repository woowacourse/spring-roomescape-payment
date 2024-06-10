package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;

import io.restassured.RestAssured;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.member.dto.MemberResponse;
import roomescape.payment.domain.PaymentRestClient;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.MyReservationWaitingResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.dto.TimeResponse;
import roomescape.time.repository.TimeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
class ReservationServiceTest {
    private static final Reservation RESERVATION_1 = new Reservation(1L,
            MEMBER_BRI, LocalDate.now().plusDays(1), TIME_1, THEME_1,
            ReservationStatus.RESERVED);
    private static final Reservation RESERVATION_2 = new Reservation(2L,
            MEMBER_BRI, LocalDate.now().plusDays(2), TIME_1, THEME_1,
            ReservationStatus.RESERVED);

    @LocalServerPort
    private int port;
    @MockBean
    private PaymentRestClient paymentRestClient;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        themeRepository.save(THEME_1);
        timeRepository.save(TIME_1);
        reservationRepository.save(RESERVATION_1);
        reservationRepository.save(RESERVATION_2);
    }

    @DisplayName("모든 예약을 조회할 수 있다.")
    @Test
    void findReservationsTest() {
        assertThat(reservationService.findReservations())
                .containsExactlyInAnyOrder(
                        ReservationResponse.from(RESERVATION_1), ReservationResponse.from(RESERVATION_2));
    }

    @DisplayName("나의 예약을 조회할 수 있다.")
    @Test
    void findMyReservationsTest() {
        assertThat(reservationService.findMyReservations(MEMBER_BRI.getId()))
                .containsExactlyInAnyOrder(
                        MyReservationWaitingResponse.from(RESERVATION_1),
                        MyReservationWaitingResponse.from(RESERVATION_2));
    }

    @DisplayName("에약을 생성할 수 있다.")
    @Test
    void createReservationTest() {
        LocalDate date = LocalDate.now().plusDays(3);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(MEMBER_BRI.getId(), date, 1L, 1L);

        ReservationResponse expected = new ReservationResponse(
                3L, MemberResponse.from(MEMBER_BRI), date, new TimeResponse(1L, TIME_1.getStartAt()),
                new ThemeResponse(1L, THEME_1.getName(), THEME_1.getDescription(), THEME_1.getThumbnail()),
                ReservationStatus.RESERVED.getName());

        assertThat(reservationService.createAdminReservation(request))
                .isEqualTo(expected);
    }

    @DisplayName("예약 생성 시, memberId에 해당하는 멤버가 없다면 예외를 던진다.")
    @Test
    void createReservationTest_whenMemberNotExist() {
        LocalDate date = LocalDate.now().plusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(100L, date, 1L, 1L);

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("해당 멤버가 존재하지 않습니다.");
    }

    @DisplayName("예약 생성 시, timeId에 해당하는 예약 시간이 없다면 예외를 던진다.")
    @Test
    void createReservationTest_whenTimeNotExist() {
        LocalDate date = LocalDate.now().plusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 100L, 1L);

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("해당 예약 시간이 존재하지 않습니다.");
    }

    @DisplayName("예약 생성 시, themeId에 해당하는 테마가 없다면 예외를 던진다.")
    @Test
    void createReservationTest_whenThemeNotExist() {
        LocalDate date = LocalDate.now().plusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 1L, 100L);

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("해당 테마가 존재하지 않습니다.");
    }

    @DisplayName("예약 생성 시, 예약 시간이 현재 시간 이전이라면 예외를 던진다.")
    @Test
    void createReservationTest_whenDateTimeIsBefore() {
        LocalDate date = LocalDate.now().minusDays(3);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(MEMBER_BRI.getId(), date, 1L, 1L);

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("예약은 현재 시간 이후여야 합니다.");
    }


    @DisplayName("예약 생성 시, 해당 예약 날짜와 시간에 이미 예약된 테마라면 예외를 던진다.")
    @Test
    void createReservationTest_whenExistsDateAndTimeAndTheme() {
        LocalDate date = LocalDate.now().plusDays(3);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(MEMBER_BRI.getId(), date, 1L, 1L);
        reservationService.createAdminReservation(request);

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("해당 날짜와 시간에 이미 예약된 테마입니다.");
    }
}
