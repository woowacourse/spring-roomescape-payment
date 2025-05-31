package roomescape.payment.toss.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.payment.toss.dto.TossPaymentRequest;
import roomescape.payment.toss.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.toss.service.TossPaymentClient;
import roomescape.payment.toss.service.TossPaymentService;


@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class TossPaymentServiceTest {

    @Autowired
    private TossPaymentService tossPaymentService;

    @MockitoBean
    private TossPaymentClient tossPaymentClient;


    @Test
    void 일시적인_결제_오류가_발생하면_재시도한다() {

        // given
        TossPaymentRequest request = new TossPaymentRequest("paymentId", "orderId", 1000L);

        given(tossPaymentClient.getPaymentConfirm(request))
                .willThrow(new PaymentTemporaryException("Temporary error"));

        // when & then
        assertThatThrownBy(() -> tossPaymentService.confirmPayment(request))
                .isInstanceOf(PaymentTemporaryException.class);

        verify(tossPaymentClient, times(3)).getPaymentConfirm(any(TossPaymentRequest.class));
    }

    @Test
    void 일시적인_오류_후에_결제가_성공할_수_있다() {
        // given
        TossPaymentRequest request = new TossPaymentRequest("paymentId", "orderId", 1000L);

        TossPaymentResponse response = new TossPaymentResponse("orderId");
        given(tossPaymentClient.getPaymentConfirm(request))
                .willThrow(new PaymentTemporaryException("Temporary error"))
                .willThrow(new PaymentTemporaryException("Temporary error"))
                .willReturn(response);

        // when & then
        assertThat(tossPaymentService.confirmPayment(request)).isEqualTo(response);

        verify(tossPaymentClient, times(3)).getPaymentConfirm(any(TossPaymentRequest.class));
    }
}
