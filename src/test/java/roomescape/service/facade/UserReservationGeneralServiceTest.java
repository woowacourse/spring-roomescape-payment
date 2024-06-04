package roomescape.service.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import io.restassured.RestAssured;
import roomescape.controller.dto.CreateReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.global.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.PaymentService;
import roomescape.service.UserReservationService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class UserReservationGeneralServiceTest {

    @LocalServerPort
    int port;

    @Autowired
    private UserReservationGeneralService reservationFacadeService;

    @Autowired
    private UserReservationService userReservationService;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    private final LocalDate date = LocalDate.parse("2060-01-01");

    private final Long timeId = 1L;
    private final Long themeId = 1L;
    private final Long userId = 1L;
    private final String orderId = "";
    private final long amount = 1000;
    private final String paymentKey = "";

    @BeforeEach
    void setUpData() {
        RestAssured.port = port;

        memberRepository.save(new Member("러너덕", "deock@test.com", "123a!", Role.USER));
        memberRepository.save(new Member("트레", "tretre@test.com", "123a!", Role.USER));
        themeRepository.save(new Theme("테마1", "d1", "https://test.com/test1.jpg"));
        reservationTimeRepository.save(new ReservationTime("08:00"));

        doNothing()
                .when(paymentService)
                .pay(any(String.class), any(Long.class), any(String.class));
    }

    @Nested
    @DisplayName("예약 생성")
    class Reserve {
        @DisplayName("성공: 예약을 저장하고, 해당 예약을 id값과 함께 반환한다.")
        @Test
        void save() {
            CreateReservationResponse saved = reservationFacadeService.reserve(orderId, amount, paymentKey, userId, date, timeId, themeId);
            assertThat(saved.id()).isEqualTo(1L);
        }

        @DisplayName("실패: 존재하지 않는 멤버 ID 입력 시 예외가 발생한다.")
        @Test
        void save_MemberIdDoesntExist() {
            assertThatThrownBy(
                    () -> reservationFacadeService.reserve(orderId, amount, paymentKey, 3L, date, timeId, themeId)
            ).isInstanceOf(RoomescapeException.class)
                    .hasMessage("입력한 사용자 ID에 해당하는 데이터가 존재하지 않습니다.");
        }

        @DisplayName("실패: 존재하지 않는 시간 ID 입력 시 예외가 발생한다.")
        @Test
        void save_TimeIdDoesntExist() {
            assertThatThrownBy(
                    () -> reservationFacadeService.reserve(orderId, amount, paymentKey, userId, date, 2L, themeId)
            ).isInstanceOf(RoomescapeException.class)
                    .hasMessage("입력한 시간 ID에 해당하는 데이터가 존재하지 않습니다.");
        }

        @DisplayName("실패: 중복 예약을 생성하면 예외가 발생한다.")
        @Test
        void save_Duplication() {
            reservationFacadeService.reserve(orderId, amount, paymentKey, userId, date, timeId, themeId);

            assertThatThrownBy(
                    () -> reservationFacadeService.reserve(orderId, amount, paymentKey, userId, date, timeId, themeId)
            ).isInstanceOf(RoomescapeException.class)
                    .hasMessage("해당 시간에 예약이 이미 존재합니다.");
        }

        @Nested
        @DisplayName("과거 일자/시간 예약 생성 시 예외 발생")
        class TimeProblemException {
            @DisplayName("실패: 과거 날짜 예약 생성하면 예외 발생 -- 어제")
            @Test
            void save_PastDateReservation() {
                LocalDate yesterday = LocalDate.now().minusDays(1);

                assertThatThrownBy(
                        () -> reservationFacadeService.reserve(orderId, amount, paymentKey, userId, yesterday, timeId, themeId)
                ).isInstanceOf(RoomescapeException.class)
                        .hasMessage("과거 예약을 추가할 수 없습니다.");
            }

            @DisplayName("실패: 같은 날짜, 과거 시간 예약 생성하면 예외 발생 -- 1분 전")
            @Test
            void save_TodayPastTimeReservation() {
                LocalDate today = LocalDate.now();
                LocalTime oneMinuteAgo = LocalTime.now().minusMinutes(1);
                oneMinuteAgo = oneMinuteAgo.withNano(0);

                ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(oneMinuteAgo.toString()));

                assertThatThrownBy(
                        () -> reservationFacadeService.reserve(orderId, amount, paymentKey, userId, today, savedTime.getId(), themeId)
                ).isInstanceOf(RoomescapeException.class)
                        .hasMessage("과거 예약을 추가할 수 없습니다.");
            }
        }
    }
}
