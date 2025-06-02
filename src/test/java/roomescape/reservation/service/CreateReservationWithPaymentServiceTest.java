package roomescape.reservation.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.auth.service.dto.LoginMember;
import roomescape.common.exception.CustomException;
import roomescape.infrastructure.TossPaymentClient;
import roomescape.infrastructure.dto.request.ConfirmPaymentRequest;
import roomescape.infrastructure.dto.response.ConfirmPaymentResponse;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.vo.PaymentStatus;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.ReservationWithPaymentResponse;
import roomescape.theme.domain.Theme;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@TestPropertySource(properties = "spring.sql.init.mode=never")
@DataJpaTest
@Import({
        CreateReservationWithPaymentService.class,
        CreateReservationService.class,
        DeleteReservationService.class,
        PaymentService.class
})
class CreateReservationWithPaymentServiceTest {

    public static final String PAYMENT_KEY = "testKey";
    public static final String ORDER_ID = "testId";
    public static final int AMOUNT = 1000;
    public static final ConfirmPaymentResponse PAYMENT_RESPONSE = new ConfirmPaymentResponse(AMOUNT, PAYMENT_KEY, null);
    public static final ConfirmPaymentRequest PAYMENT_REQUEST = new ConfirmPaymentRequest(PAYMENT_KEY, ORDER_ID, AMOUNT);

    private final LocalDateTime now = LocalDateTime.now();
    private final Theme theme = new Theme("공포", "으악", "www.um.com");
    private final ReservationTime time = new ReservationTime(LocalTime.of(8, 0));
    private final Member member = new Member("밍곰", "test@test.com", "12341234", Role.MEMBER);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoBean
    private TossPaymentClient mockTossPaymentClient = Mockito.mock(TossPaymentClient.class);

    @Autowired
    private CreateReservationWithPaymentService service;

    @BeforeEach
    void setup() {
        entityManager.persist(theme);
        entityManager.persist(time);
        entityManager.persist(member);
    }

    @DisplayName("토스 페이먼츠 API 응답이 400으로 실패한 경우, 예약은 생성되지 않고 실패 결제 내역만 저장된다.")
    @Test
    void noReservationAndFailedPayment4xxResponse() {
        // given
        Mockito.when(mockTossPaymentClient.postConfirmPayment(PAYMENT_REQUEST)).thenReturn(ResponseEntity.status(400).body(PAYMENT_RESPONSE));
        ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(
                now.plusDays(1).toLocalDate(),
                time.getId(),
                theme.getId(),
                PAYMENT_KEY,
                ORDER_ID,
                AMOUNT
        );

        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> {
                service.create(request, LoginMember.of(member));
            }).isInstanceOf(CustomException.class);

            List<Payment> payments = paymentRepository.findAll();
            softly.assertThat(payments).hasSize(1);
            softly.assertThat(payments.getFirst().getStatus()).isSameAs(PaymentStatus.FAILED);

            softly.assertThat(reservationRepository.findAll()).hasSize(0);
        });
    }

    @DisplayName("토스 페이먼츠 API 응답이 500으로 실패한 경우, 예약은 생성되지 않고 실패 결제 내역만 저장된다.")
    @Test
    void noReservationAndFailedPayment5xxResponse() {
        // given
        Mockito.when(mockTossPaymentClient.postConfirmPayment(PAYMENT_REQUEST)).thenReturn(ResponseEntity.status(500).body(PAYMENT_RESPONSE));
        ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(
                now.plusDays(1).toLocalDate(),
                time.getId(),
                theme.getId(),
                PAYMENT_KEY,
                ORDER_ID,
                AMOUNT
        );

        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> {
                service.create(request, LoginMember.of(member));
            }).isInstanceOf(CustomException.class);

            List<Payment> payments = paymentRepository.findAll();
            softly.assertThat(payments).hasSize(1);
            softly.assertThat(payments.getFirst().getStatus()).isSameAs(PaymentStatus.FAILED);

            softly.assertThat(reservationRepository.findAll()).hasSize(0);
        });
    }

    @DisplayName("토스 페이먼츠 API 응답이 성공한 경우, 예약과 성공 결제 내역이 저장된다.")
    @Test
    void reservationAndCompletePayment2xxResponse() {
        // given
        Mockito.when(mockTossPaymentClient.postConfirmPayment(PAYMENT_REQUEST)).thenReturn(ResponseEntity.status(200).body(PAYMENT_RESPONSE));
        ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(
                now.plusDays(1).toLocalDate(),
                time.getId(),
                theme.getId(),
                PAYMENT_KEY,
                ORDER_ID,
                AMOUNT
        );

        // when
        ReservationWithPaymentResponse response = service.create(request, LoginMember.of(member));

        // then
        assertSoftly(softly -> {
            List<Payment> payments = paymentRepository.findAll();
            softly.assertThat(payments).hasSize(1);
            softly.assertThat(payments.getFirst().getStatus()).isSameAs(PaymentStatus.COMPLETE);

            List<Reservation> reservations = reservationRepository.findAll();
            softly.assertThat(reservations).hasSize(1);
            softly.assertThat(reservations.stream().findFirst().get().getId()).isEqualTo(response.id());
        });
    }
}
