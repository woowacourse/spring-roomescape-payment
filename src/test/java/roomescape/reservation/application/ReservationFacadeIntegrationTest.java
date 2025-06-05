package roomescape.reservation.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.auth.sign.password.Password;
import roomescape.common.domain.Email;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResult;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.domain.WaitingReservationRepository;
import roomescape.reservation.ui.dto.AvailableReservationTimeWebResponse;
import roomescape.reservation.ui.dto.CreateReservationWithUserIdWebRequest;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;
import roomescape.user.domain.User;
import roomescape.user.domain.UserName;
import roomescape.user.domain.UserRepository;
import roomescape.user.domain.UserRole;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationFacadeIntegrationTest {

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WaitingReservationRepository waitingReservationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private PaymentClient paymentClient;

    private User user;
    private Theme theme;
    private ReservationTime time;

    @BeforeEach
    void setUp() {
        user = userRepository.save(
                User.of(
                        UserName.from("테스트사용자"),
                        Email.from("test@example.com"),
                        Password.fromEncoded("encoded-password"),
                        UserRole.NORMAL
                )
        );

        theme = themeRepository.save(
                Theme.of(
                        ThemeName.from("테스트테마"),
                        ThemeDescription.from("테마 설명"),
                        ThemeThumbnail.from("http://example.com/image.jpg")
                )
        );

        time = timeRepository.save(
                ReservationTime.from(LocalTime.of(15, 0))
        );
    }

    @Test
    @DisplayName("특정 날짜와 테마의 사용 가능한 예약 시간을 조회할 수 있다")
    void getAvailable() {
        // given
        LocalDate targetDate = LocalDate.now().plusDays(1);

        ReservationTime anotherTime = timeRepository.save(
                ReservationTime.from(LocalTime.of(16, 0))
        );

        reservationRepository.save(
                Reservation.of(
                        user.getId(),
                        ReservationDate.from(targetDate),
                        time,
                        theme
                )
        );

        // when
        List<AvailableReservationTimeWebResponse> responses = reservationFacade.getAvailable(
                targetDate,
                theme.getId()
        );

        // then
        assertThat(responses).isNotEmpty();
        assertThat(responses)
                .anyMatch(response -> response.timeId().equals(anotherTime.getId()) && !response.isBooked())
                .anyMatch(response -> response.timeId().equals(time.getId()) && response.isBooked());
    }

    @Test
    @DisplayName("예약 삭제 후, 대기된 예약이 있을 시 승격시킨다.")
    void deleteAndPromotionWhenExistsWaiting() {
        // given
        long userId1 = 1L;
        final Reservation reservation = reservationRepository.save(Reservation.of(userId1,
                ReservationDate.from(LocalDate.now().plusDays(1)),
                time,
                theme
        ));
        long userId2 = 2L;
        final WaitingReservation waitingReservation = waitingReservationRepository.save(WaitingReservation.of(userId2,
                1,
                ReservationDate.from(LocalDate.now().plusDays(1)),
                time,
                theme
        ));
        //when
        reservationFacade.delete(reservation.getId());
        //then
        assertThat(waitingReservationRepository.findAll()).isEmpty();
        assertThat(reservationRepository.findAllByUserId(userId1)).isEmpty();
        assertThat(reservationRepository.findAllByUserId(userId2).getFirst().getUserId())
                .isEqualTo(waitingReservation.getUserId());
    }

    @Nested
    @DisplayName("예약 생성 및 결제 시 트랜잭션 롤백 테스트 ")
    class CreateWithMockPaymentClient {

        @Test
        @DisplayName("결제 검증 실패 시, 예약이 데이터베이스에 저장되지 않는다")
        void createWithPaymentWhenPaymentVerificationFails() {
            // given
            CreateReservationWithUserIdWebRequest reservationRequest = createReservationRequest();

            PaymentRequest paymentRequest = mock(PaymentRequest.class);
            PaymentResult paymentResult = mock(PaymentResult.class);
            given(paymentClient.confirmPayment(any())).willReturn(paymentResult);

            given(paymentResult.verifyPayment(paymentRequest, paymentResult))
                    .willReturn(false);

            int initialReservationCount = countReservations();

            // when
            // then
            assertThatThrownBy(() ->
                    reservationFacade.createWithPayment(reservationRequest, paymentRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("결제 요청이 잘못되었습니다. 관리자에게 문의해주세요.");

            int finalReservationCount = countReservations();
            assertThat(finalReservationCount).isEqualTo(initialReservationCount);

            assertThat(reservationRepository.findAllByUserId(user.getId())).isEmpty();
        }

        @Test
        @DisplayName("결제 클라이언트 호출 자체가 실패하는 경우에도 예약이 롤백된다")
        void createWithPaymentWhenPaymentClientThrowsException() {
            CreateReservationWithUserIdWebRequest reservationRequest = createReservationRequest();
            PaymentRequest paymentRequest = mock(PaymentRequest.class);

            given(paymentClient.confirmPayment(any()))
                    .willThrow(new RuntimeException("결제 서비스와의 연결에 실패했습니다"));

            int initialReservationCount = countReservations();

            // when
            // then
            assertThatThrownBy(() ->
                    reservationFacade.createWithPayment(reservationRequest, paymentRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("결제 서비스와의 연결에 실패했습니다");

            int finalReservationCount = countReservations();
            assertThat(finalReservationCount).isEqualTo(initialReservationCount);
            assertThat(reservationRepository.findAllByUserId(user.getId())).isEmpty();
        }

        private CreateReservationWithUserIdWebRequest createReservationRequest() {
            return new CreateReservationWithUserIdWebRequest(
                    LocalDate.now().plusDays(1),
                    time.getId(),
                    theme.getId(),
                    user.getId()
            );
        }

        private int countReservations() {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reservations", Integer.class);
        }
    }
}
