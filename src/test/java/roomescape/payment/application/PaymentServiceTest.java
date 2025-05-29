package roomescape.payment.application;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import roomescape.payment.domain.PaymentInfo;
import roomescape.reservation.application.dto.MemberReservationRequest;

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
        PaymentInfo paymentInfo = new PaymentInfo("key", "orderId", 1000L);
        MemberReservationRequest request = createRequest(
            LocalDate.of(2030, 3, 3),
            1L, 1L, paymentInfo);
        PaymentRequest paymentRequest = new PaymentRequest(
            request.date(), request.timeId(), request.themeId(), request.paymentKey(),
            request.orderId(), request.amount()
        );
        TossConfirmRequest tossConfirmRequest = new TossConfirmRequest(paymentRequest.paymentKey(),
            paymentRequest.orderId(), paymentRequest.amount());

        TossConfirmResponse tossConfirmResponse = new TossConfirmResponse(
            tossConfirmRequest.paymentKey(), tossConfirmRequest.orderId(),
            new EasyPayInfo(paymentRequest.amount()));

        when(tossPaymentGatewayClient.processPaymentConfirm(tossConfirmRequest))
            .thenReturn(tossConfirmResponse);

        // when
        Payment payment = paymentService.addPayment(paymentRequest, 1L);

        // then
        assertAll(
            () -> assertThat(payment.getId()).isNotNull(),
            () -> assertThat(payment.getPaymentInfo().equals(paymentInfo))
        );
}

private MemberReservationRequest createRequest(LocalDate now, Long timeId, Long themeId,
    PaymentInfo paymentInfo) {
    return new MemberReservationRequest(now, timeId, themeId, paymentInfo.getPaymentKey(),
        paymentInfo.getOrderId(), paymentInfo.getAmount());
    }
}


