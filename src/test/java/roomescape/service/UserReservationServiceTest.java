package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import roomescape.controller.dto.response.MyReservationResponse;
import roomescape.controller.dto.response.ReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Payment;
import roomescape.domain.theme.Theme;
import roomescape.global.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class UserReservationServiceTest {
    private final LocalDate date = LocalDate.parse("2060-01-01");
    private final Long timeId = 1L;
    private final Long themeId = 1L;
    private final Long paymentId = 1L;
    private final Long userId = 1L;
    private final Long adminId = 2L;

    @LocalServerPort
    int port;

    @Autowired
    private UserReservationService userReservationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @BeforeEach
    void setUpData() {
        RestAssured.port = port;

        memberRepository.save(new Member("러너덕", "deock@test.com", "123a!", Role.USER));
        memberRepository.save(new Member("트레", "tretre@test.com", "123a!", Role.USER));
        themeRepository.save(new Theme("테마1", "d1", "https://test.com/test1.jpg"));
        paymentRepository.save(new Payment("orderId", 1000, "paymenyKey"));
        reservationTimeRepository.save(new ReservationTime("08:00"));
    }

    @Nested
    @DisplayName("예약 생성")
    class Reserve {
        @DisplayName("성공: 예약을 저장하고, 해당 예약을 id값과 함께 반환한다.")
        @Test
        void save() {
            ReservationResponse saved = userReservationService.reserve(new CreateReservationRequest(userId, date, timeId, themeId), paymentId);
            assertThat(saved.id()).isEqualTo(1L);
        }

        @DisplayName("실패: 존재하지 않는 멤버 ID 입력 시 예외가 발생한다.")
        @Test
        void save_MemberIdDoesntExist() {
            assertThatThrownBy(
                    () -> userReservationService.reserve(new CreateReservationRequest(3L, date, timeId, themeId), paymentId)
            ).isInstanceOf(RoomescapeException.class)
                    .hasMessage("입력한 사용자 ID에 해당하는 데이터가 존재하지 않습니다.");
        }

        @DisplayName("실패: 존재하지 않는 시간 ID 입력 시 예외가 발생한다.")
        @Test
        void save_TimeIdDoesntExist() {
            assertThatThrownBy(
                    () -> userReservationService.reserve(new CreateReservationRequest(userId, date, 2L, themeId), paymentId)
            ).isInstanceOf(RoomescapeException.class)
                    .hasMessage("입력한 시간 ID에 해당하는 데이터가 존재하지 않습니다.");
        }

        @DisplayName("실패: 중복 예약을 생성하면 예외가 발생한다.")
        @Test
        void save_Duplication() {
            userReservationService.reserve(new CreateReservationRequest(userId, date, timeId, themeId), paymentId);

            assertThatThrownBy(
                    () -> userReservationService.reserve(new CreateReservationRequest(userId, date, timeId, themeId), paymentId)
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
                        () -> userReservationService.reserve(new CreateReservationRequest(userId, yesterday, timeId, themeId), paymentId)
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
                        () -> userReservationService.reserve(new CreateReservationRequest(userId, today, savedTime.getId(), themeId), paymentId)
                ).isInstanceOf(RoomescapeException.class)
                        .hasMessage("과거 예약을 추가할 수 없습니다.");
            }
        }
    }

    @Nested
    @DisplayName("예약 대기 생성")
    class StandBy {
        @DisplayName("성공: 예약 대기")
        @Test
        void standby() {
            ReservationResponse saved = userReservationService.standby(userId, new CreateUserReservationStandbyRequest(date, timeId, themeId));
            assertThat(saved.id()).isEqualTo(1L);
        }

        @DisplayName("실패: 본인의 예약에 대기를 걸 수 없다.")
        @Test
        void standby_CantReserveAndThenStandbyForTheSameReservation() {
            userReservationService.reserve(new CreateReservationRequest(userId, date, timeId, themeId), paymentId);

            assertThatThrownBy(() -> userReservationService.standby(userId, new CreateUserReservationStandbyRequest(date, timeId, themeId)))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage("이미 예약 혹은 대기가 있습니다.");
        }

        @DisplayName("실패: 하나의 예약에 두 개 이상 대기를 걸 수 없다.")
        @Test
        void standby_CantStandbyMoreThanOnce() {
            userReservationService.reserve(new CreateReservationRequest(adminId, date, timeId, themeId), paymentId);
            userReservationService.standby(userId, new CreateUserReservationStandbyRequest(date, timeId, themeId));

            assertThatThrownBy(() -> userReservationService.standby(userId, new CreateUserReservationStandbyRequest(date, timeId, themeId)))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage("이미 예약 혹은 대기가 있습니다.");
        }
    }

    @Nested
    @DisplayName("예약/예약 대기 조회")
    class findMyReservationsWithRank {
        @DisplayName("성공: 특정 멤버가 예약한 예약 및 예약대기 목록 조회")
        @Test
        void findAllWithRankByMemberId() {
            userReservationService.reserve(new CreateReservationRequest(adminId, LocalDate.parse("2060-01-01"), timeId, themeId), paymentId);
            userReservationService.standby(userId, new CreateUserReservationStandbyRequest(LocalDate.parse("2060-01-01"), timeId, themeId));
            userReservationService.reserve(new CreateReservationRequest(userId, LocalDate.parse("2060-01-02"), timeId, themeId), paymentId);
            userReservationService.reserve(new CreateReservationRequest(userId, LocalDate.parse("2060-01-03"), timeId, themeId), paymentId);

            List<MyReservationResponse> reservations = userReservationService.findMyReservationsWithRank(userId);
            assertAll(
                    () -> assertThat(reservations)
                            .extracting(response -> response.reservation().id())
                            .containsExactly(2L, 3L, 4L),
                    () -> assertThat(reservations)
                            .extracting(MyReservationResponse::rank)
                            .containsExactly(1L, 0L, 0L)
            );
        }

        @Nested
        @DisplayName("예약 대기 삭제")
        class DeleteStandBy {
            @DisplayName("성공: 일반유저는 본인의 예약대기를 삭제할 수 있다.")
            @Test
            void deleteStandby() {
                userReservationService.reserve(new CreateReservationRequest(adminId, date, timeId, themeId), paymentId);
                userReservationService.standby(userId, new CreateUserReservationStandbyRequest(date, timeId, themeId));
                Member user = memberRepository.findById(userId).get();

                assertThatCode(() -> userReservationService.deleteStandby(2L, user))
                        .doesNotThrowAnyException();
            }

            @DisplayName("실패: 일반유저는 타인의 예약대기를 삭제할 수 없다.")
            @Test
            void deleteStandby_ReservedByOther() {
                userReservationService.reserve(new CreateReservationRequest(userId, date, timeId, themeId), paymentId);
                userReservationService.standby(adminId, new CreateUserReservationStandbyRequest(date, timeId, themeId));
                Member user = memberRepository.findById(userId).get();

                assertThatThrownBy(() -> userReservationService.deleteStandby(2L, user))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage("자신의 예약만 삭제할 수 있습니다.");
            }

            @DisplayName("실패: 예약대기 삭제 메서드로 예약을 삭제할 수 없다.")
            @Test
            void deleteStandby_Cannot_Delete_Reserved() {
                userReservationService.reserve(new CreateReservationRequest(userId, date, timeId, themeId), paymentId);
                userReservationService.standby(adminId, new CreateUserReservationStandbyRequest(date, timeId, themeId));
                Member user = memberRepository.findById(userId).get();

                assertThatThrownBy(() -> userReservationService.deleteStandby(1L, user))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage("예약대기가 존재하지 않아 삭제할 수 없습니다.");
            }
        }
    }
}
