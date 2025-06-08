package roomescape.reservation.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.auth.service.dto.LoginMember;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.EntityNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.payment.infraStructure.PaymentClientSelector;
import roomescape.payment.infraStructure.toss.TossPaymentClient;
import roomescape.payment.infraStructure.dto.request.ConfirmPaymentRequest;
import roomescape.payment.infraStructure.dto.response.ConfirmPaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.ReservationResponse;
import roomescape.reservation.service.dto.response.ReservationTimeResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.dto.response.ThemeResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles("test")
@DataJpaTest
@Import({CreateReservationService.class, PaymentClientSelector.class})
public class CreateReservationWithPaymentTest {

    private final LocalDateTime now = LocalDateTime.now();
    private final Theme theme = new Theme("포스티", "공포", "wwww.um.com");
    private final ReservationTime time = new ReservationTime(LocalTime.of(8, 0));
    private final Member member = new Member("포스티", "test@test.com", "12341234", Role.MEMBER);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CreateReservationService reservationService;

    @MockitoBean
    private TossPaymentClient mockTossPaymentClient = Mockito.mock(TossPaymentClient.class);

    @BeforeEach
    void setup() {
        entityManager.persist(theme);
        entityManager.persist(time);
        entityManager.persist(member);
    }

    @DisplayName("멤버의 결제 및 예약을 생성한다.")
    @Test
    void createMembersPaymentAndReservation() {
        // given
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentClient.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        LocalDate date = now.plusDays(1).toLocalDate();
        ReservationWithPaymentRequest reservationWithPaymentRequest = new ReservationWithPaymentRequest(
                date,
                time.getId(),
                theme.getId(),
                paymentResponse.paymentKey(),
                paymentRequest.orderId(),
                1000,
                "TOSS"
        );

        // when
        ReservationResponse result = reservationService.createWithPayment(reservationWithPaymentRequest, LoginMember.of(member));

        // then
        assertSoftly(softly -> {
            softly.assertThat(entityManager.find(Reservation.class, result.id())).isNotNull();
            softly.assertThat(result.member().name()).isEqualTo(member.getName());
            softly.assertThat(result.date()).isEqualTo(date);
            softly.assertThat(result.time()).isEqualTo(new ReservationTimeResponse(time.getId(), time.getStartAt()));
            softly.assertThat(result.theme()).isEqualTo(new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail()));
        });
    }

    @DisplayName("멤버가 중복 예약을 시도하면 실패한다.")
    @Test
    void failCreateMembersPaymentAndReservation() {
        // given
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentClient.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        LocalDate date = now.plusDays(1).toLocalDate();
        ReservationWithPaymentRequest reservationWithPaymentRequest = new ReservationWithPaymentRequest(
                date,
                time.getId(),
                theme.getId(),
                paymentResponse.paymentKey(),
                paymentRequest.orderId(),
                1000,
                "TOSS"
        );

        // when
        ReservationResponse result = reservationService.createWithPayment(reservationWithPaymentRequest, LoginMember.of(member));

        //then
        Assertions.assertThatThrownBy(
                () -> reservationService.createWithPayment(reservationWithPaymentRequest, LoginMember.of(member)))
                .isInstanceOf(DuplicatedException.class)
                .hasMessage("중복되는 예약이 존재합니다.");
    }

    @DisplayName("멤버가 존재하지 않는 예약 아이디로로 예약을 시도하면 실패한다.")
    @Test
    void failCreateMembersPaymentAndReservation2() {
        // given
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentClient.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        LocalDate date = now.plusDays(1).toLocalDate();
        ReservationWithPaymentRequest reservationWithPaymentRequest = new ReservationWithPaymentRequest(
                date,
                0L,
                theme.getId(),
                paymentResponse.paymentKey(),
                paymentRequest.orderId(),
                1000,
                "TOSS"
        );

        //when
        //then
        Assertions.assertThatThrownBy(
                        () -> reservationService.createWithPayment(reservationWithPaymentRequest, LoginMember.of(member)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 예약 가능 시간입니다.");
    }

    @DisplayName("멤버가 존재하지 않는 테마 아이디로 예약을 시도하면 실패한다.")
    @Test
    void failCreateMembersPaymentAndReservation3() {
        // given
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentClient.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        LocalDate date = now.plusDays(1).toLocalDate();
        ReservationWithPaymentRequest reservationWithPaymentRequest = new ReservationWithPaymentRequest(
                date,
                time.getId(),
                0L,
                paymentResponse.paymentKey(),
                paymentRequest.orderId(),
                1000,
                "TOSS"
        );

        // when
        //then
        Assertions.assertThatThrownBy(
                        () -> reservationService.createWithPayment(reservationWithPaymentRequest, LoginMember.of(member)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 테마입니다.");
    }

    @DisplayName("멤버가 지난 날짜로 예약을 시도하면 실패한다.")
    @Test
    void failCreateMembersPaymentAndReservation4() {
        // given
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentClient.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        LocalDate date = now.minusDays(1).toLocalDate();
        ReservationWithPaymentRequest reservationWithPaymentRequest = new ReservationWithPaymentRequest(
                date,
                time.getId(),
                theme.getId(),
                paymentResponse.paymentKey(),
                paymentRequest.orderId(),
                1000,
                "TOSS"
        );

        // when
        //then
        Assertions.assertThatThrownBy(
                        () -> reservationService.createWithPayment(reservationWithPaymentRequest, LoginMember.of(member)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("과거 날짜의 예약은 생성할 수 없습니다.");
    }

    @Test
    void test(){
        // given

        // when
        // then
    }
}
