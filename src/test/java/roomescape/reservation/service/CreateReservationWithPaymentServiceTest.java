package roomescape.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.auth.service.dto.LoginMember;
import roomescape.common.exception.CustomException;
import roomescape.common.exception.InternalServerErrorException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.payment.config.PaymentRestClientConfig;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.vo.PaymentStatus;
import roomescape.payment.infrastructure.TossPaymentClient;
import roomescape.payment.infrastructure.TossPaymentErrorHandler;
import roomescape.payment.infrastructure.dto.PaymentFailure;
import roomescape.payment.infrastructure.dto.response.ConfirmPaymentResponse;
import roomescape.payment.infrastructure.vo.TossPaymentInternalErrorCode;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.ReservationWithPaymentResponse;
import roomescape.theme.domain.Theme;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Transactional
@AutoConfigureDataJpa
@AutoConfigureTestEntityManager
@TestPropertySource(properties = "spring.sql.init.mode=never")
@RestClientTest
@Import({
        PaymentRestClientConfig.class,
        ReservationCommandService.class,
        PaymentService.class,
        TossPaymentErrorHandler.class
})
class CreateReservationWithPaymentServiceTest {

    public static final String PAYMENT_KEY = "testKey";
    public static final String ORDER_ID = "testId";
    public static final long AMOUNT = 1_000L;

    private final LocalDateTime now = LocalDateTime.now();
    private final Theme theme = new Theme("공포", "으악", "www.um.com");
    private final ReservationTime time = new ReservationTime(LocalTime.of(8, 0));
    private final Member member = new Member("밍곰", "test@test.com", "12341234", Role.MEMBER);

    private ReservationWithPaymentRequest request;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TossPaymentErrorHandler tossPaymentErrorHandler;

    @Autowired
    private ReservationCommandService reservationCommandService;

    @Autowired
    private PaymentService paymentService;

    private CreateReservationWithPaymentService service;

    @Autowired
    private RestClient.Builder restClientBuilder;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        entityManager.persist(theme);
        entityManager.persist(time);
        entityManager.persist(member);

        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        TossPaymentClient tossPaymentClient = new TossPaymentClient(
                restClientBuilder.build(), tossPaymentErrorHandler
        );
        service = new CreateReservationWithPaymentService(
                tossPaymentClient,
                reservationCommandService,
                paymentService
        );

        request = new ReservationWithPaymentRequest(
                now.plusDays(1).toLocalDate(),
                time.getId(),
                theme.getId(),
                PAYMENT_KEY,
                ORDER_ID,
                AMOUNT
        );
    }

    @DisplayName("토스 페이먼츠 API 응답이 400으로 실패한 경우, 취소 상태의 예약과 실패 결제 내역이 저장된다.")
    @Test
    void noReservationAndFailedPayment4xxResponse() {
        // given
        String jsonResponse = getBadRequestFailedJsonResponse();
        mockServer.expect(requestTo("/confirm"))
                        .andExpect(method(HttpMethod.POST))
                        .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jsonResponse)
                        );

        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> {
                service.create(request, LoginMember.of(member));
            }).isInstanceOf(CustomException.class);

            List<Payment> payments = paymentRepository.findAll();
            softly.assertThat(payments).hasSize(1);
            softly.assertThat(payments.getFirst().getStatus()).isSameAs(PaymentStatus.FAILED);

            List<Reservation> reservations = reservationRepository.findAll();
            softly.assertThat(reservations).hasSize(1);
            softly.assertThat(reservations.getFirst().getStatus()).isSameAs(ReservationStatus.CANCELED);
        });
    }

    @DisplayName("토스 페이먼츠 API 응답이 500으로 실패한 경우, 취소 상태의 예약과 실패 결제 내역이 저장된다.")
    @Test
    void noReservationAndFailedPayment5xxResponse() {
        // given
        String jsonResponse = getInternalServerFailedJsonResponse();
        mockServer.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonResponse)
                );

        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> {
                service.create(request, LoginMember.of(member));
            }).isInstanceOf(InternalServerErrorException.class);

            List<Payment> payments = paymentRepository.findAll();
            softly.assertThat(payments).hasSize(1);
            softly.assertThat(payments.getFirst().getStatus()).isSameAs(PaymentStatus.FAILED);

            List<Reservation> reservations = reservationRepository.findAll();
            softly.assertThat(reservations).hasSize(1);
            softly.assertThat(reservations.getFirst().getStatus()).isSameAs(ReservationStatus.CANCELED);
        });
    }

    @DisplayName("토스 페이먼츠 API 응답이 성공한 경우, 예약과 성공 결제 내역이 저장된다.")
    @Test
    void reservationAndCompletePayment2xxResponse() {
        // given
        String jsonResponse = getOkJsonResponse();
        mockServer.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonResponse)
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

    private String getBadRequestFailedJsonResponse() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(
                    new ConfirmPaymentResponse(
                            AMOUNT,
                            PAYMENT_KEY,
                            new PaymentFailure("FOO", null)
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("JSON 파싱 과정에서 오류 발생");
        }
    }

    private String getInternalServerFailedJsonResponse() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(
                    new ConfirmPaymentResponse(
                            AMOUNT,
                            PAYMENT_KEY,
                            new PaymentFailure(TossPaymentInternalErrorCode.INVALID_API_KEY.name(), null)
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("JSON 파싱 과정에서 오류 발생");
        }
    }

    private String getOkJsonResponse() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(
                    new ConfirmPaymentResponse(
                            AMOUNT,
                            PAYMENT_KEY,
                            null
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("JSON 파싱 과정에서 오류 발생");
        }
    }
}
