package roomescape.payment.client;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.ClientPaymentException;
import roomescape.payment.dto.request.TossPaymentConfirmRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(TossPaymentClient.class)
public class PaymentPaymentKeyTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public RestClient tossRestClient(MockServerRestClientCustomizer customizer) {
            String secretKey = "CURRENT_SECRET_KEY";
            String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

            RestClient.Builder builder = RestClient.builder()
                    .baseUrl("https://api.tosspayments.com/v1")
                    .defaultHeader("Authorization", "Basic " + encodedAuth)
                    .defaultHeader("Content-Type", "application/json");
            customizer.customize(builder);
            return builder.build();
        }
    }

    @Autowired
    TossPaymentClient tossPaymentClient;

    @Autowired
    MockRestServiceServer mockServer;

    @Test
    void 클라이언트에서_획득하지_않은_페이먼트_키로_요청_시_실패한다() {
        // given
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(header("Authorization", "Basic Q1VSUkVOVF9TRUNSRVRfS0VZOg=="))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                    { "message": "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다." }
                                """));

        TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                ""
        );

        Assertions.assertThatThrownBy(() -> tossPaymentClient.confirmPayment(tossPaymentConfirmRequest))
                .isInstanceOf(ClientPaymentException.class)
                .hasMessage("결제 실패 : 결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }
}
