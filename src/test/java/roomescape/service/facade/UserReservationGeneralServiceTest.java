package roomescape.service.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import roomescape.controller.dto.request.CreateUserReservationRequest;
import roomescape.controller.dto.response.ReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.global.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.PaymentService;
import roomescape.service.UserReservationService;
import roomescape.service.dto.PaymentRequest;

@SpringBootTest
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class UserReservationGeneralServiceTest {
    private final LocalDate date = LocalDate.parse("2060-01-01");
    private final Long timeId = 1L;
    private final Long themeId = 1L;
    private final Long userId = 1L;
    private final String orderId = "";
    private final long amount = 1000;
    private final String paymentKey = "";
    private final String paymentType = "카드";

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private UserReservationGeneralService reservationFacadeService;

    @Autowired
    private UserReservationService userReservationService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @BeforeEach
    void setUpData() {
        memberRepository.save(new Member("러너덕", "deock@test.com", "123a!", Role.USER));
        memberRepository.save(new Member("트레", "tretre@test.com", "123a!", Role.USER));
        themeRepository.save(new Theme("테마1", "d1", "https://test.com/test1.jpg"));
        reservationTimeRepository.save(new ReservationTime("08:00"));

        Payment payment = new Payment(orderId, amount, paymentKey);
        paymentRepository.save(payment);
        when(paymentService.pay(any(PaymentRequest.class)))
                .thenReturn(payment);
    }

    @Nested
    @DisplayName("예약 생성")
    class Reserve {
        @DisplayName("성공: 예약을 저장하고, 해당 예약을 id값과 함께 반환한다.")
        @Test
        void save() {
            CreateUserReservationRequest request = new CreateUserReservationRequest(date, themeId, timeId, paymentKey, orderId, amount, paymentType);
            ReservationResponse saved = reservationFacadeService.reserve(userId, request);
            assertThat(saved.id()).isEqualTo(1L);
        }

        @DisplayName("실패: 존재하지 않는 멤버 ID 입력 시 예외가 발생한다.")
        @Test
        void save_MemberIdDoesntExist() {
            CreateUserReservationRequest request = new CreateUserReservationRequest(date, themeId, timeId, paymentKey, orderId, amount, paymentType);
            assertThatThrownBy(() -> reservationFacadeService.reserve(3L, request))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage("입력한 사용자 ID에 해당하는 데이터가 존재하지 않습니다.");
        }

        @DisplayName("실패: 존재하지 않는 시간 ID 입력 시 예외가 발생한다.")
        @Test
        void save_TimeIdDoesntExist() {
            CreateUserReservationRequest request = new CreateUserReservationRequest(date, themeId, 2L, paymentKey, orderId, amount, paymentType);

            assertThatThrownBy(() -> reservationFacadeService.reserve(userId, request))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage("입력한 시간 ID에 해당하는 데이터가 존재하지 않습니다.");
        }

        @DisplayName("실패: 중복 예약을 생성하면 예외가 발생한다.")
        @Test
        void save_Duplication() {
            CreateUserReservationRequest request = new CreateUserReservationRequest(date, themeId, timeId, paymentKey, orderId, amount, paymentType);
            reservationFacadeService.reserve(userId, request);

            assertThatThrownBy(() -> reservationFacadeService.reserve(userId, request))
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
                CreateUserReservationRequest request = new CreateUserReservationRequest(yesterday, themeId, timeId, paymentKey, orderId, amount, paymentType);

                assertThatThrownBy(() -> reservationFacadeService.reserve(userId, request))
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
                CreateUserReservationRequest request = new CreateUserReservationRequest(today, themeId, savedTime.getId(), paymentKey, orderId, amount, paymentType);

                assertThatThrownBy(() -> reservationFacadeService.reserve(userId, request))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage("과거 예약을 추가할 수 없습니다.");
            }
        }
    }
}
