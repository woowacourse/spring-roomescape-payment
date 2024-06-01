package roomescape.core.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.payment.PaymentResponse;
import roomescape.core.exception.PaymentException;
import roomescape.core.repository.PaymentRepository;
import roomescape.infrastructure.PaymentClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @MockBean
    private PaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        BDDMockito.doNothing()
                .when(paymentClient).approvePayment(any(), any());

        BDDMockito.doNothing()
                .when(paymentClient).refundPayment(any(), any());
    }

    @Test
    @DisplayName("결제를 승인에 성공하면 결제 정보가 DB에 저장된다.")
    void savePaymentInfoIfApproved() {
        PaymentRequest paymentRequest = new PaymentRequest("test_payment_key", "test_order_id", 1000L);

        paymentService.approvePayment(paymentRequest);

        assertThat(paymentRepository.findById(6L).isPresent()).isTrue();
    }

    @Test
    @DisplayName("결제를 승인에 실패하면 예외가 발생하고 결제 정보가 DB에 저장되지 않는다.")
    void throwExceptionAndDoNotSavePaymentInfoUnlessApproved() {
        BDDMockito.doThrow(new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 승인 과정에서 문제가 발생했습니다."))
                .when(paymentClient).approvePayment(any(), any());
        PaymentRequest paymentRequest = new PaymentRequest("test_payment_key", "test_order_id", 1000L);

        assertAll(
                () -> assertThatThrownBy(() -> paymentService.approvePayment(paymentRequest))
                        .isInstanceOf(PaymentException.class),
                () -> assertThat(paymentRepository.findById(6L).isEmpty()).isTrue()
        );
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
        PaymentResponse paymentResponse = new PaymentResponse(5L, "payment_key5", 1000L, "order_id5");

        paymentService.refundPayment(paymentResponse);

        assertThat(paymentRepository.findById(5L).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("환불에 실패하면 예외가 발생하고 결제 정보가 DB에서 삭제되지 않는다.")
    void throwExceptionAndDoNotDeletePaymentUnlessSucceedRefund() {
        BDDMockito.doThrow(new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "환불 과정에서 문제가 발생했습니다."))
                .when(paymentClient).refundPayment(any(), any());
        PaymentResponse paymentResponse = new PaymentResponse(5L, "payment_key5", 1000L, "order_id5");

        assertAll(
                () -> assertThatThrownBy(() -> paymentService.refundPayment(paymentResponse))
                        .isInstanceOf(PaymentException.class),
                () -> assertThat(paymentRepository.findById(5L).isPresent()).isTrue()
        );
    }
}
