package roomescape.waiting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.fixture.MemberFixture.MEMBER_ADMIN;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.MemberFixture.MEMBER_BROWN;
import static roomescape.fixture.MemberFixture.MEMBER_DUCK;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;

import io.restassured.RestAssured;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.MyReservationWaitingResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.repository.TimeRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingWithOrder;
import roomescape.waiting.dto.WaitingCreateRequest;
import roomescape.waiting.dto.WaitingResponse;
import roomescape.waiting.repository.WaitingRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class WaitingServiceTest {
    private static final Reservation RESERVATION_1 = new Reservation(1L,
            MEMBER_BRI, LocalDate.now().plusDays(1), TIME_1, THEME_1,
            ReservationStatus.RESERVED);
    private static final Waiting WAITING_1 = new Waiting(RESERVATION_1, MEMBER_BROWN);
    private static final Reservation RESERVATION_2 = new Reservation(2L,
            MEMBER_BRI, LocalDate.now().plusDays(2), TIME_1, THEME_1,
            ReservationStatus.RESERVED);
    private static final Waiting WAITING_2 = new Waiting(RESERVATION_2, MEMBER_BROWN);

    @LocalServerPort
    private int port;
    @Autowired
    private WaitingService waitingService;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private WaitingRepository waitingRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        themeRepository.save(THEME_1);
        timeRepository.save(TIME_1);
        reservationRepository.save(RESERVATION_1);
        reservationRepository.save(RESERVATION_2);
        waitingRepository.save(WAITING_1);
        waitingRepository.save(WAITING_2);
    }

    @DisplayName("나의 예약 대기 목록을 조회할 수 있다.")
    @Test
    void findMyWaitingTest() {
        assertThat(waitingService.findMyWaitings(2L))
                .containsExactlyInAnyOrder(
                        MyReservationWaitingResponse.from(new WaitingWithOrder(WAITING_1, 1L)),
                        MyReservationWaitingResponse.from(new WaitingWithOrder(WAITING_2, 1L))
                );
    }

    @DisplayName("예약 대기를 생성할 수 있다.")
    @Test
    void createWaitingTest() {
        WaitingCreateRequest request =
                new WaitingCreateRequest(RESERVATION_1.getDate(), RESERVATION_1.getThemeId(), RESERVATION_1.getTimeId());
        WaitingResponse response =
                new WaitingResponse(3L, ReservationResponse.from(RESERVATION_1), MemberResponse.from(MEMBER_DUCK));

        assertThat(waitingService.createWaiting(request, MEMBER_DUCK.getId()))
                .isEqualTo(response);
    }

    @DisplayName("예약 대기시, 예약이 존재하지 않는다면 예외를 던진다.")
    @Test
    void createWaitingTest_whenReservationNotExist() {
        WaitingCreateRequest request =
                new WaitingCreateRequest(LocalDate.now(), 100L, 100L);

        assertThatThrownBy(() -> waitingService.createWaiting(request, MEMBER_ADMIN.getId()))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("존재하지 않는 예약에 대해 대기할 수 없습니다.");
    }
}
