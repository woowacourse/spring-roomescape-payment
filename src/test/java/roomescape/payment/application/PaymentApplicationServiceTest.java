package roomescape.payment.application;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.config.TestConfig;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.exception.PaymentKeyDuplicatedException;
import roomescape.payment.infrastructure.PaymentRepository;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(TestConfig.class)
class PaymentApplicationServiceTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private PaymentClient paymentClient;

    private PaymentApplicationService paymentApplicationService;

    @BeforeEach
    void setUp() {
        when(paymentClient.approvePayment(any())).thenAnswer(invocation -> {
            PaymentApproveRequest req = invocation.getArgument(0);
            return new PaymentApproveResponse(req.paymentKey(), req.orderId(), req.amount());
        });
        PaymentDataService paymentDataService = new PaymentDataService(paymentRepository);
        paymentApplicationService = new PaymentApplicationService(paymentDataService, paymentClient);
    }

    @Test
    void 정상적으로_결제_승인_후_결제_레코드가_생성된다() {
        // Given
        PaymentApproveRequest paymentApproveRequest = new PaymentApproveRequest("testtest", "orderorder", 1000L);

        // When
        Payment actual = paymentApplicationService.approveReservationPayment(paymentApproveRequest);

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getId()).isNotNull();
            softAssertions.assertThat(actual.getPaymentKey()).isEqualTo(paymentApproveRequest.paymentKey());
            softAssertions.assertThat(actual.getOrderId()).isEqualTo(paymentApproveRequest.orderId());
            softAssertions.assertThat(actual.getAmount()).isEqualTo(paymentApproveRequest.amount());
        });
    }

    @Test
    void 중복된_paymentKey는_저장될_수_없다() {
        // Given
        PaymentApproveRequest paymentApproveRequest1 = new PaymentApproveRequest("testtest", "orderorder", 1000L);
        PaymentApproveRequest paymentApproveRequest2 = new PaymentApproveRequest("testtest", "orderorder", 1000L);

        paymentApplicationService.approveReservationPayment(paymentApproveRequest1);

        // When & Then
        assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequest2))
                .isInstanceOf(PaymentKeyDuplicatedException.class)
                .hasMessage("중복된 paymentKey입니다.");
    }

    @Test
    void paymentKey는_null이나_빈_값이_될_수_없다() {
        // Given
        PaymentApproveRequest paymentApproveRequestWithPaymentKeyNull = new PaymentApproveRequest(null, "orderorder", 1000L);
        PaymentApproveRequest paymentApproveRequestWithPaymentKeyBlank = new PaymentApproveRequest("", "orderorder", 1000L);

        // When & Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithPaymentKeyNull))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("키가 올바르지 않습니다.");
            softAssertions.assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithPaymentKeyBlank))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("키가 올바르지 않습니다.");
        });
    }

    @Test
    void 주문_번호는_null이나_빈_값이_될_수_없다() {
        // Given
        PaymentApproveRequest paymentApproveRequestWithOrderIdNull = new PaymentApproveRequest("testtest", null, 1000L);
        PaymentApproveRequest paymentApproveRequestWithOrderIdBlank = new PaymentApproveRequest("testtest", "", 1000L);

        // When & Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithOrderIdNull))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("주문 번호가 올바르지 않습니다.");
            softAssertions.assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithOrderIdBlank))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("주문 번호가 올바르지 않습니다.");
        });
    }

    @Test
    void 결제_금액은_null이_될_수_없다() {
        // Given
        PaymentApproveRequest paymentApproveRequestWithAmountNull = new PaymentApproveRequest("testtest", "orderorder", null);

        // When & Then
        assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithAmountNull))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액은 null이 될 수 없습니다.");
    }

    @Test
    void 결제_금액은_최소_결제_금액_이상이어야_한다() {
        // Given
        PaymentApproveRequest paymentApproveRequestWithAmountUnderUnitPrice = new PaymentApproveRequest("testtest", "orderorder", 900L);

        // When & Then
        assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithAmountUnderUnitPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액이 잘못되었습니다.");
    }

    @Test
    void 결제_금액은_최소_결제_금액으로_나누어_떨어져야_한다() {
        // Given
        PaymentApproveRequest paymentApproveRequestWithAmountUnderUnitPrice = new PaymentApproveRequest("testtest", "orderorder", 999L);

        // When & Then
        assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithAmountUnderUnitPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액이 잘못되었습니다.");
    }
}
