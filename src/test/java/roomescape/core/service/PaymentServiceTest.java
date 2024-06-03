package roomescape.core.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import roomescape.core.domain.Reservation;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.exception.PaymentException;
import roomescape.core.repository.PaymentRepository;
import roomescape.core.repository.ReservationRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
class PaymentServiceTest {

    private static final String TOSS_ERROR_BODY_EXAMPLE = "{\"code\": \"NOT_FOUND_PAYMENT\", \"message\": \"존재하지 않는 결제 입니다.\"}";

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RestTemplate paymentApproveRestTemplate;
    @Autowired
    private RestTemplate paymentRefundRestTemplate;
    private MockRestServiceServer paymentApproveMockServer;
    private MockRestServiceServer paymentRefundMockServer;

    @BeforeEach
    void setUp() {
        paymentApproveMockServer = MockRestServiceServer.bindTo(paymentApproveRestTemplate).build();
        paymentRefundMockServer = MockRestServiceServer.bindTo(paymentRefundRestTemplate).build();
    }

    @Test
    @DisplayName("결제 승인에 성공하면 결제 정보가 DB에 저장된다.")
    void savePaymentInfoIfApproved() {
        paymentApproveMockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess());
        Reservation reservation = reservationRepository.findById(5L).get();
        PaymentRequest paymentRequest = new PaymentRequest("test_payment_key", "test_order_id", 1000L);

        paymentService.approvePayment(reservation, paymentRequest);

        assertThat(paymentRepository.findById(5L).isPresent()).isTrue();
        paymentApproveMockServer.verify();
    }

    @Test
    @DisplayName("결제 승인에 실패하면 예외가 발생하고 결제 정보가 DB에 저장되지 않는다.")
    void throwExceptionAndDoNotSavePaymentInfoUnlessApproved() {
        paymentApproveMockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withServerError().body(TOSS_ERROR_BODY_EXAMPLE));
        Reservation reservation = reservationRepository.findById(5L).get();
        PaymentRequest paymentRequest = new PaymentRequest("test_payment_key", "test_order_id", 1000L);

        assertAll(
                () -> assertThatThrownBy(() -> paymentService.approvePayment(reservation, paymentRequest))
                        .isInstanceOf(PaymentException.class),
                () -> assertThat(paymentRepository.findById(5L).isEmpty()).isTrue()
        );
        paymentApproveMockServer.verify();
    }

    @Test
    @DisplayName("결제 승인을 위해 필요한 인증 헤더를 생성한다.")
    void createPaymentAuthorization() {
        PaymentAuthorizationResponse paymentAuthorization = paymentService.createPaymentAuthorization();

        assertThat(paymentAuthorization.getPaymentAuthorization()).isNotNull();
    }

    @Test
    @DisplayName("환불에 성공하면 결제 정보가 DB에서 삭제된다.")
    void deletePaymentIfSucceedRefund() {
        paymentRefundMockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/payment_key1/cancel"))
                .andRespond(withSuccess());
        Reservation reservation = reservationRepository.findById(1L).get();

        paymentService.refundPayment(reservation);

        assertThat(paymentRepository.findById(1L).isEmpty()).isTrue();
        paymentRefundMockServer.verify();
    }

    @Test
    @DisplayName("환불에 실패하면 예외가 발생하고 결제 정보가 DB에서 삭제되지 않는다.")
    void throwExceptionAndDoNotDeletePaymentUnlessSucceedRefund() {
        paymentRefundMockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/payment_key1/cancel"))
                .andRespond(withServerError().body(TOSS_ERROR_BODY_EXAMPLE));
        Reservation reservation = reservationRepository.findById(1L).get();

        assertAll(
                () -> assertThatThrownBy(() -> paymentService.refundPayment(reservation))
                        .isInstanceOf(PaymentException.class),
                () -> assertThat(paymentRepository.findById(1L).isPresent()).isTrue()
        );
        paymentRefundMockServer.verify();
    }
}
