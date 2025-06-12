package roomescape.infrastructure.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentResponse;

@RestClientTest(TossPaymentClient.class)
class TossPaymentClientTest {

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    @DisplayName("토스 결제 승인 요청 API를 호출한다.")
    void confirmPayment() {
        // given
        PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "ROOM_ESCAPE_test_order_id", 1000);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                            "paymentKey": "paymentKey",
                            "orderId": "ROOM_ESCAPE_test_order_id",
                            "orderName": "방탈출 예약 1건",
                            "totalAmount": 1000
                        }
                        """, MediaType.APPLICATION_JSON));

        // when
        PaymentResponse result = paymentClient.confirmPayment(paymentInfo);

        // then
        assertAll(() -> assertThat(result.paymentKey()).isEqualTo(paymentInfo.paymentKey()),
                () -> assertThat(result.orderId()).isEqualTo(paymentInfo.orderId()),
                () -> assertThat(result.totalAmount()).isEqualTo(paymentInfo.amount()));
    }


}
