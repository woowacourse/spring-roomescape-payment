package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import io.restassured.RestAssured;
import roomescape.controller.dto.request.CreateReservationRequest;
import roomescape.controller.dto.request.CreateUserReservationStandbyRequest;
import roomescape.controller.dto.request.SearchReservationFilterRequest;
import roomescape.controller.dto.response.ReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.global.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class AdminReservationServiceTest {
    private final LocalDate date = LocalDate.parse("2060-01-01");
    private final Long timeId = 1L;
    private final Long themeId = 1L;
    private final Long userId = 1L;
    private final Long adminId = 2L;

    @LocalServerPort
    int port;

    @Autowired
    private AdminReservationService adminReservationService;

    @Autowired
    private UserReservationService userReservationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @BeforeEach
    void setUpData() {
        RestAssured.port = port;
        memberRepository.save(new Member("러너덕", "deock@test.com", "123a!", Role.USER));
        memberRepository.save(new Member("트레", "tretre@test.com", "123a!", Role.USER));

        themeRepository.save(new Theme("테마1", "d1", "https://test.com/test1.jpg"));
        themeRepository.save(new Theme("테마2", "d2", "https://test.com/test2.jpg"));

        reservationTimeRepository.save(new ReservationTime("08:00"));
    }

    @Nested
    @DisplayName("예약 생성")
    class Reserve {
        @DisplayName("성공: 예약을 저장하고, 해당 예약을 id값과 함께 반환한다.")
        @Test
        void save() {
            ReservationResponse saved = adminReservationService.reserve(
                    new CreateReservationRequest(userId, date, timeId, themeId));
            assertThat(saved.id()).isEqualTo(1L);
        }

        @DisplayName("실패: 존재하지 않는 멤버 ID 입력 시 예외가 발생한다.")
        @Test
        void save_MemberIdDoesntExist() {
            assertThatThrownBy(() -> adminReservationService.reserve(
                    new CreateReservationRequest(3L, date, timeId, themeId)))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage("입력한 사용자 ID에 해당하는 데이터가 존재하지 않습니다.");
        }

        @DisplayName("실패: 존재하지 않는 시간 ID 입력 시 예외가 발생한다.")
        @Test
        void save_TimeIdDoesntExist() {
            assertThatThrownBy(() -> adminReservationService.reserve(
                    new CreateReservationRequest(userId, date, 2L, themeId)))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage("입력한 시간 ID에 해당하는 데이터가 존재하지 않습니다.");
        }

        @DisplayName("실패: 중복 예약을 생성하면 예외가 발생한다.")
        @Test
        void save_Duplication() {
            adminReservationService.reserve(
                    new CreateReservationRequest(userId, date, timeId, themeId));

            assertThatThrownBy(() -> adminReservationService.reserve(
                    new CreateReservationRequest(userId, date, timeId, themeId)))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage("해당 시간에 예약이 이미 존재합니다.");
        }

        @Nested
        @DisplayName("과거 일자/시간 예약 생성 시 예외 발생")
        class TimeProblemException {
            @DisplayName("실패: 과거 날짜 예약 생성하면 예외 발생 -- 어제")
            @Test
            void save_PastDateReservation() {
                LocalDate yesterday = LocalDate.now().minusDays(1);

                assertThatThrownBy(() -> adminReservationService.reserve(
                        new CreateReservationRequest(userId, yesterday, timeId, themeId)))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage("과거 예약을 추가할 수 없습니다.");
            }

            @DisplayName("실패: 같은 날짜, 과거 시간 예약 생성하면 예외 발생 -- 1분 전")
            @Test
            void save_TodayPastTimeReservation() {
                LocalDate today = LocalDate.now();
                LocalTime oneMinuteAgo = LocalTime.now().minusMinutes(1);
                oneMinuteAgo = oneMinuteAgo.withNano(0);

                ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(oneMinuteAgo.toString()));

                assertThatThrownBy(() -> adminReservationService.reserve(
                        new CreateReservationRequest(userId, today, savedTime.getId(), themeId)))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage("과거 예약을 추가할 수 없습니다.");
            }
        }
    }

    @Nested
    @DisplayName("예약 삭제")
    class DeleteById {
        @DisplayName("성공: 예약 삭제")
        @Test
        void deleteById() {
            adminReservationService.reserve(new CreateReservationRequest(adminId, LocalDate.parse("2060-01-01"), timeId, themeId));
            adminReservationService.reserve(new CreateReservationRequest(adminId, LocalDate.parse("2060-01-02"), timeId, themeId));
            adminReservationService.reserve(new CreateReservationRequest(adminId, LocalDate.parse("2060-01-03"), timeId, themeId));

            adminReservationService.deleteById(2L);

            assertThat(adminReservationService.findAllReserved())
                    .extracting(ReservationResponse::id)
                    .containsExactly(1L, 3L);
        }
    }

    @Nested
    @DisplayName("예약 대기 삭제")
    class DeleteStandby {
        @DisplayName("성공: 다른 회원의 예약대기 삭제")
        @Test
        void deleteStandby_ByAdmin() {
            adminReservationService.reserve(new CreateReservationRequest(adminId, date, timeId, themeId));
            userReservationService.standby(userId, new CreateUserReservationStandbyRequest(date, timeId, themeId));

            assertThatCode(() -> adminReservationService.deleteStandby(2L))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("전체 예약 조회")
    class findAllReserved {
        @DisplayName("성공: 전체 예약 조회")
        @Test
        void findAllReserved() {
            adminReservationService.reserve(new CreateReservationRequest(adminId, LocalDate.parse("2060-01-01"), timeId, themeId));
            adminReservationService.reserve(new CreateReservationRequest(userId, LocalDate.parse("2060-01-02"), timeId, themeId));
            adminReservationService.reserve(new CreateReservationRequest(adminId, LocalDate.parse("2060-01-03"), timeId, themeId));

            assertThat(adminReservationService.findAllReserved())
                    .extracting(ReservationResponse::id)
                    .containsExactly(1L, 2L, 3L);
        }
    }

    @Nested
    @DisplayName("전체 예약 대기 조회")
    class findAllStandby {
        @DisplayName("성공: 전체 대기 조회")
        @Test
        void findAllStandby() {
            adminReservationService.reserve(new CreateReservationRequest(adminId, date, timeId, themeId));
            userReservationService.standby(userId, new CreateUserReservationStandbyRequest(date, timeId, themeId));

            assertThat(adminReservationService.findAllStandby())
                    .extracting(ReservationResponse::id)
                    .containsExactly(2L);
        }
    }

    @Nested
    @DisplayName("검색 세부 조회")
    class findAllByFilter {
        @DisplayName("성공: 검색 필터에 따라 조회")
        @Test
        void findAllByFilter() {
            adminReservationService.reserve(new CreateReservationRequest(adminId, LocalDate.parse("2060-01-01"), 1L, 1L));
            adminReservationService.reserve(new CreateReservationRequest(adminId, LocalDate.parse("2060-01-02"), 1L, 2L));
            adminReservationService.reserve(new CreateReservationRequest(adminId, LocalDate.parse("2060-01-03"), 1L, 1L));
            adminReservationService.reserve(new CreateReservationRequest(userId, LocalDate.parse("2060-01-04"), 1L, 2L));
            adminReservationService.reserve(new CreateReservationRequest(userId, LocalDate.parse("2060-01-05"), 1L, 1L));
            adminReservationService.reserve(new CreateReservationRequest(userId, LocalDate.parse("2060-01-06"), 1L, 2L));

            List<ReservationResponse> reservations = adminReservationService.findAllByFilter(
                    new SearchReservationFilterRequest(2L, userId, LocalDate.parse("2060-01-01"), LocalDate.parse("2060-01-06")));

            assertThat(reservations)
                    .extracting(ReservationResponse::id)
                    .containsExactly(4L, 6L);
        }
    }
}
