package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.custom.EntityNotFoundException;
import roomescape.common.exception.custom.PaymentClientException;
import roomescape.common.exception.custom.PaymentServerException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.config.PaymentTestConfig;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentMethod;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.error.ClientErrorCode;
import roomescape.payment.error.InternalServerErrorCode;
import roomescape.payment.gateway.PaymentGatewayResolver;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

@ActiveProfiles("test")
@DataJpaTest
@Import(PaymentTestConfig.class)
class PaymentServiceTest {

    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl("https://api.tosspayments.com");
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(testBuilder).build();

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentGatewayResolver paymentGatewayResolver;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, reservationRepository, paymentGatewayResolver);
    }

    @DisplayName("결제를 추가한다.")
    @Test
    void create() {
        // given
        Reservation savedReservation = createReservation();

        server.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess("""
                        {
                          "paymentKey": "test-key",
                          "orderId": "test-order-id",
                          "amount": 10000,
                          "method": "TOSS"
                        }
                        """, APPLICATION_JSON));

        // when
        PaymentRequest paymentRequest = new PaymentRequest(
                "test-key",
                "test-order-id",
                10000L,
                PaymentMethod.TOSS
        );
        paymentService.create(savedReservation.getId(), paymentRequest);

        // then
        Payment payment = paymentRepository.findAll().getFirst();
        assertThat(paymentRepository.findAll()).hasSize(1);
        assertThat(payment.getPaymentKey()).isEqualTo("test-key");
        assertThat(payment.getOrderId()).isEqualTo("test-order-id");
        assertThat(payment.getAmount()).isEqualTo(10000L);
    }

    @DisplayName("결제 생성 중 예외가 발생한다.")
    @Test
    void create_throwsException() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                "test-key",
                "test-order-id",
                10000L,
                PaymentMethod.TOSS
        );

        // when & then
        assertThatThrownBy(() -> paymentService.create(1L, paymentRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("결제를 승인한다.")
    @Test
    void confirm() {
        // given
        server.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess("""
                        {
                          "paymentKey": "confirm-key",
                          "orderId": "confirm-order-id",
                          "amount": 10000,
                          "method": "TOSS"
                        }
                        """, APPLICATION_JSON));

        PaymentRequest request = new PaymentRequest(
                "confirm-key",
                "confirm-order-id",
                10000L,
                PaymentMethod.TOSS
        );

        // when & then
        assertThatCode(() -> paymentService.confirm(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("결제 승인 중 클라이언트 예외가 발생한다.")
    @Test
    void confirm_throwsClientException() {
        // given
        server.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withBadRequest().body("""
                        {
                          "code": "ALREADY_PROCESSED_PAYMENT",
                          "message": "이미 처리된 결제 입니다."
                        }
                        """).contentType(APPLICATION_JSON));

        PaymentRequest request = new PaymentRequest(
                "already-processed-key",
                "already-processed-order-id",
                5000L,
                PaymentMethod.TOSS
        );

        // when & then
        assertThatThrownBy(() -> paymentService.confirm(request))
                .isInstanceOf(PaymentClientException.class)
                .hasMessageContaining(ClientErrorCode.ALREADY_PROCESSED_PAYMENT.getMessage());
    }

    @DisplayName("결제 승인 중 서버 예외가 발생한다.")
    @Test
    void confirm_throwsServerException() {
        // given
        server.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withServerError().body("""
                        {
                          "code": "UNAUTHORIZED_KEY",
                          "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                        }
                        """).contentType(APPLICATION_JSON));

        PaymentRequest request = new PaymentRequest(
                "server-error-key",
                "server-error-order-id",
                9999L,
                PaymentMethod.TOSS
        );

        // when & then
        assertThatThrownBy(() -> paymentService.confirm(request))
                .isInstanceOf(PaymentServerException.class)
                .hasMessageContaining(InternalServerErrorCode.UNAUTHORIZED_KEY.getMessage());
    }

    private Reservation createReservation() {
        Theme theme = themeRepository.save(new Theme("우테코", "우테코 테마", "www.woowacourse.com"));
        ReservationTime time = timeRepository.save(new ReservationTime(LocalTime.now()));
        Member member = memberRepository.save(new Member("하루", "haru@haru.com", "12341234", Role.ADMIN));
        return reservationRepository.save(new Reservation(member, LocalDate.now(), time, theme));
    }
}
