package roomescape.payment.application;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.TossConfirmRequest;
import roomescape.payment.application.dto.TossConfirmResponse;
import roomescape.payment.application.dto.TossConfirmResponse.EasyPayInfo;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentGateway;
import roomescape.payment.infra.TossPaymentGatewayClient;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class PaymentServiceTest {

    @MockitoBean
    private TossPaymentGatewayClient tossPaymentGatewayClient;

    @Autowired
    private PaymentService paymentService;

    @Test
    void 결제_저장_테스트() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("key", "orderId", 1000L);
        TossConfirmRequest tossConfirmRequest = new TossConfirmRequest(paymentRequest.paymentKey(),
            paymentRequest.orderId(), paymentRequest.amount());

        TossConfirmResponse tossConfirmResponse = new TossConfirmResponse(
            tossConfirmRequest.paymentKey(), tossConfirmRequest.orderId(),
            new EasyPayInfo(paymentRequest.amount()));

        when(tossPaymentGatewayClient.processPaymentConfirm(tossConfirmRequest))
            .thenReturn(tossConfirmResponse);

        // when
        Payment payment = paymentService.addPayment(paymentRequest);

        // then
        assertAll(
            () -> assertThat(payment.getId()).isNotNull(),
            () -> assertThat(payment.getPaymentKey().equals(paymentRequest.paymentKey())),
            () -> assertThat(payment.getOrderId().equals(paymentRequest.orderId())),
            () -> assertThat(payment.getAmount()).isEqualTo(paymentRequest.amount()),
            () -> assertThat(payment.getPaymentGateway()).isEqualTo(PaymentGateway.TOSS_PAYMENTS)
        );
    }
}
