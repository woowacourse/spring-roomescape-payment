package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.CleanUp;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.infra.toss.client.TossPaymentClient;
import roomescape.payment.infra.toss.dto.TossPaymentRequest;
import roomescape.payment.infra.toss.dto.TossPaymentResponse;


@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CleanUp cleanUp;

    @MockitoBean
    private TossPaymentClient tossPaymentClient;

    @BeforeEach
    void setUp() {
        cleanUp.all();
    }

    @Test
    void 일시적인_결제_오류가_발생하면_재시도한다() {

        // given
        PaymentRequest request = new PaymentRequest("paymentId", "orderId", 1000L);
        TossPaymentRequest tossRequest = TossPaymentRequest.from(request);

        given(tossPaymentClient.getPaymentConfirm(tossRequest))
                .willThrow(new PaymentTemporaryException("Temporary error"));

        // when & then
        assertThatThrownBy(() -> paymentService.confirmPayment(request))
                .isInstanceOf(PaymentTemporaryException.class);

        verify(tossPaymentClient, times(2)).getPaymentConfirm(any(TossPaymentRequest.class));
    }

    @Test
    void 일시적인_오류_후에_결제가_성공할_수_있다() {
        // given
        String orderId = "orderId";
        String paymentId = "paymentId";
        PaymentRequest request = new PaymentRequest(paymentId, orderId, 1000L);
        TossPaymentRequest tossPaymentRequest = TossPaymentRequest.from(request);

        TossPaymentResponse response = new TossPaymentResponse(orderId, paymentId);
        given(tossPaymentClient.getPaymentConfirm(tossPaymentRequest))
                .willThrow(new PaymentTemporaryException("Temporary error"))
                .willReturn(response);

        // when & then
        assertThat(paymentService.confirmPayment(request)).isEqualTo(response.toPaymentResponse());

        verify(tossPaymentClient, times(2)).getPaymentConfirm(any(TossPaymentRequest.class));
    }
}
