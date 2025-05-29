package roomescape.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.payment.application.dto.TossConfirmRequest;
import roomescape.payment.application.dto.TossConfirmResponse;

@RestClientTest(TossPaymentGatewayClient.class)
class TossPaymentGatewayClientTest {

    @Autowired
    private TossPaymentGatewayClient tossPaymentGatewayClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 결제_승인_요청을_처리한다() throws Exception {
        // given
        TossConfirmRequest request = new TossConfirmRequest("key", "orderId", 1000L);
        TossConfirmResponse expected = new TossConfirmResponse("key", "orderId",
            new TossConfirmResponse.EasyPayInfo(1000L));

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
            .andRespond(withSuccess(
                objectMapper.writeValueAsString(expected),
                MediaType.APPLICATION_JSON
            ));

        // when
        TossConfirmResponse response = tossPaymentGatewayClient.processPaymentConfirm(request);

        // then
        assertAll(
            () -> assertThat(response.paymentKey()).isEqualTo("key"),
            () -> assertThat(response.orderId()).isEqualTo("orderId"),
            () -> assertThat(response.easyPay().amount()).isEqualTo(1000L)
        );
    }
}
