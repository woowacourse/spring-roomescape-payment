package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.client.TossPaymentClient;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.client.dto.response.TossErrorResponse;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.common.exception.PaymentException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.dto.request.LoginMember;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.ReservationWithPaymentResponse;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//@DataJpaTest("")
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ReservationPaymentFacadeTest {

    private final Theme theme = Theme.createWithoutId("재미난 테마", "우끼끼", "썸네일)");
    private final ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(10, 0));
    private final Member member = Member.createWithoutId("포라", "forarium20@gmail.com", "1234", Role.USER);

    private static final TossPaymentResponse TOSS_PAYMENT_RESPONSE = new TossPaymentResponse(
            "paymentKey",
            "orderId",
            "orderName",
            "status",
            null,
            "card",
            1000L,
            null,
            null
    );

    private static final TossPaymentResponse TOSS_PAYMENT_400_ERROR_RESPONSE = new TossPaymentResponse(
            "paymentKey",
            "orderId",
            "orderName",
            "status",
            null,
            "card",
            1000L,
            null,
            new TossErrorResponse("INVALID_CARD", "유효하지 않은 카드입니다.")
    );
    private static final TossPaymentResponse TOSS_PAYMENT_500_ERROR_RESPONSE = new TossPaymentResponse(
            "paymentKey",
            "orderId",
            "orderName",
            "status",
            null,
            "card",
            1000L,
            null,
            new TossErrorResponse("INVALID_SEVER", "서버 오류입니다.")
    );

    @PersistenceContext
    EntityManager em;

    @Autowired
    ReservationPaymentFacade reservationPaymentFacade;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private TossPaymentClient mockTossPaymentClient = Mockito.mock(TossPaymentClient.class);

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private TossPaymentClient tossPaymentClient;

    @BeforeEach
    void setUp() {
        em.persist(theme);
        em.persist(reservationTime);
        em.persist(member);
        em.flush();
        em.clear();
    }

    @Test
    void 응답이_2xx일_때_결제가_성공적으로_처리된다() {
        // given
        Mockito.when(mockTossPaymentClient.confirmPayment(any(TossPaymentConfirmRequest.class)))
                .thenReturn(ResponseEntity.status(200).body(TOSS_PAYMENT_RESPONSE));

        ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(
                LocalDate.now().plusDays(1),
                reservationTime.getId(),
                theme.getId(),
                "paymentKey",
                "orderId",
                1000L
        );
        LoginMember loginMember = new LoginMember(member.getId(), member.getName());

        // when
        ReservationWithPaymentResponse reservationAndSavePayment = reservationPaymentFacade.createReservationAndSavePayment(request, loginMember);

        // then
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(1);
        assertThat(reservations.getFirst().getTheme().getName()).isEqualTo(reservationAndSavePayment.theme().name());
    }

    @Test
    void 응답이_4xx일_때_결제가_실패한다() {
        // given
        Mockito.when(mockTossPaymentClient.confirmPayment(any(TossPaymentConfirmRequest.class)))
                .thenReturn(ResponseEntity.status(400).body(TOSS_PAYMENT_400_ERROR_RESPONSE));
        doThrow(new PaymentException(HttpStatus.BAD_REQUEST, "결제 실패"))
                .when(tossPaymentClient)
                .handleTosPamentException(any(ResponseEntity.class));

        ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(
                LocalDate.now().plusDays(1),
                reservationTime.getId(),
                theme.getId(),
                "paymentKey",
                "orderId",
                1000L
        );
        LoginMember loginMember = new LoginMember(member.getId(), member.getName());

        // when & then
        assertThatThrownBy(() -> reservationPaymentFacade.createReservationAndSavePayment(request, loginMember))
                .isInstanceOf(PaymentException.class);

        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(0);
    }


    @Test
    void 응답이_5xx일_때_결제가_실패한다() {
        // given
        Mockito.when(mockTossPaymentClient.confirmPayment(any(TossPaymentConfirmRequest.class)))
                .thenReturn(ResponseEntity.status(500).body(TOSS_PAYMENT_500_ERROR_RESPONSE));
        doThrow(new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 실패"))
                .when(tossPaymentClient)
                .handleTosPamentException(any(ResponseEntity.class));

        ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(
                LocalDate.now().plusDays(1),
                reservationTime.getId(),
                theme.getId(),
                "paymentKey",
                "orderId",
                1000L
        );
        LoginMember loginMember = new LoginMember(member.getId(), member.getName());

        // when & then
        assertThatThrownBy(() -> reservationPaymentFacade.createReservationAndSavePayment(request, loginMember))
                .isInstanceOf(PaymentException.class);

        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(0);
    }
}
