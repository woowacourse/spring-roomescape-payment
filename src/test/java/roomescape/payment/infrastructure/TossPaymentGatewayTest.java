package roomescape.payment.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentConfirmFailException;

@SpringBootTest
class TossPaymentGatewayTest {

    @Autowired
    private PaymentGateway paymentGateway;

    @Autowired
    private PaymentProperties paymentProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder tossRestClientBuilder = RestClient.builder()
                .baseUrl(paymentProperties.getBaseUrl());
        mockServer = MockRestServiceServer.bindTo(tossRestClientBuilder)
                .build();
        paymentGateway = new TossPaymentGateway(tossRestClientBuilder.build(), objectMapper);
    }

    @DisplayName("토스 외부 요청이 성공하면 어떠한 예외도 발생하지 않는다.")
    @Test
    void confirm() {
        mockServer.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        assertDoesNotThrow(() -> paymentGateway.confirm("1234", 1000L, "payemtKey"));
    }

    @DisplayName("토스 외부 요청이 원활하지 않으면 예외가 발생한다.")
    @Test
    void confirmWithException() {
        String expectedBody = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제입니다."
                }
                """;

        mockServer.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(expectedBody)
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> paymentGateway.confirm("1234", 100L, "payKey"))
                .isInstanceOf(PaymentConfirmFailException.class)
                .hasMessage("존재하지 않는 결제입니다.");
    }
}
