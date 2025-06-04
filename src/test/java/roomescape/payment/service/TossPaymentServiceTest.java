package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.CleanUp;
import roomescape.payment.client.TossPaymentClient;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.repository.OrdersRepository;


@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class TossPaymentServiceTest {

    @Autowired
    private TossPaymentService tossPaymentService;

    @Autowired
    private OrdersRepository ordersRepository;

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
        TossPaymentRequest request = new TossPaymentRequest("paymentId", "orderId", 1000L);

        given(tossPaymentClient.getPaymentConfirm(request))
                .willThrow(new PaymentTemporaryException("Temporary error"));

        // when & then
        assertThatThrownBy(() -> tossPaymentService.confirmPayment(request))
                .isInstanceOf(PaymentTemporaryException.class);

        verify(tossPaymentClient, times(2)).getPaymentConfirm(any(TossPaymentRequest.class));
    }

    @Test
    void 일시적인_오류_후에_결제가_성공할_수_있다() {
        // given
        String orderId = "orderId";
        String paymentId = "paymentId";
        TossPaymentRequest request = new TossPaymentRequest(paymentId, orderId, 1000L);

        TossPaymentResponse response = new TossPaymentResponse(orderId, paymentId);
        given(tossPaymentClient.getPaymentConfirm(request))
                .willThrow(new PaymentTemporaryException("Temporary error"))
                .willReturn(response);

        // when & then
        assertThat(tossPaymentService.confirmPayment(request)).isEqualTo(response);

        verify(tossPaymentClient, times(2)).getPaymentConfirm(any(TossPaymentRequest.class));
    }

    @Test
    void 결제_정보를_저장한다() {
        // given
        TossPaymentRequest tossPaymentRequest = new TossPaymentRequest("paymentKey", "orderId", 1000L);

        // when
        TossPaymentResponse result = tossPaymentService.createOrder(tossPaymentRequest);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.orderId()).isEqualTo(tossPaymentRequest.orderId());
            softly.assertThat(result.paymentKey()).isEqualTo(tossPaymentRequest.paymentKey());
        });

        assertThat(ordersRepository.findByPaymentKey(result.paymentKey())).isPresent();
    }

}
