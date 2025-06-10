package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.OrdersRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class OrderServiceTest {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderService paymentService;

    @Test
    void 결제_정보를_저장한다() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", 1000L);

        // when
        PaymentResponse result = paymentService.createOrder(paymentRequest);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.orderId()).isEqualTo(paymentRequest.orderId());
            softly.assertThat(result.paymentKey()).isEqualTo(paymentRequest.paymentKey());
        });

        assertThat(ordersRepository.findByPaymentKey(result.paymentKey())).isPresent();
    }

}
