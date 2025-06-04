package roomescape.infrastructure.thirdparty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import roomescape.application.exception.PaymentException;
import roomescape.presentation.dto.request.PaymentProcessRequest;

@RestClientTest(TossPaymentRestClient.class)
@TestPropertySource(properties = {
        "toss.payment.api.base-url=http://localhost:8080",
        "toss.payment.api.key=test_sk_key"
})
class TossPaymentRestClientTest {

    @Autowired
    private TossPaymentRestClient tossPaymentRestClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("결제 승인 요청 성공시 응답을 반환한다")
    void getPaymentResponse_Success() throws Exception {
        PaymentProcessRequest request = new PaymentProcessRequest("paymentKey123", "orderId123", "10000");
        String successResponse = """
                {
                    "paymentKey": "paymentKey123",
                    "orderId": "orderId123",
                    "status": "DONE"
                }
                """;

        mockConfirmEndpoint(request, withSuccess(successResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<String> paymentResponse = tossPaymentRestClient.getPaymentResponse(request);

        assertThat(paymentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(paymentResponse.getBody()).isEqualTo(successResponse);
        mockServer.verify();
    }

    @Test
    @DisplayName("결제 승인 요청 실패시 PaymentException을 던진다")
    void getPaymentResponse_Failure() throws Exception {
        PaymentProcessRequest request = new PaymentProcessRequest("invalidKey", "orderId123", "10000");
        String errorResponse = """
                {
                    "code": "INVALID_PAYMENT_KEY",
                    "message": "유효하지 않은 paymentKey입니다."
                }
                """;

        mockConfirmEndpoint(request, withStatus(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse)
        );

        assertThatThrownBy(() -> tossPaymentRestClient.getPaymentResponse(request))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("유효하지 않은 paymentKey입니다.")
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);

        mockServer.verify();
    }

    @Test
    @DisplayName("토스 서버 에러시 INTERNAL_SERVER_ERROR로 PaymentException을 던진다")
    void getPaymentResponse_ServerError() throws Exception {
        PaymentProcessRequest request = new PaymentProcessRequest("paymentKey123", "orderId123", "10000");
        String errorResponse = """
                {
                    "code": "INTERNAL_SERVER_ERROR",
                    "message": "일시적인 오류가 발생했습니다."
                }
                """;

        mockConfirmEndpoint(request, withServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse)
        );

        assertThatThrownBy(() -> tossPaymentRestClient.getPaymentResponse(request))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("일시적인 오류가 발생했습니다.")
                .hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR);

        mockServer.verify();
    }

    private void mockConfirmEndpoint(PaymentProcessRequest request, ResponseCreator responseCreator) throws Exception {
        mockServer.expect(requestTo("http://localhost:8080/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic dGVzdF9za19rZXk6"))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(request)))
                .andRespond(responseCreator);
    }
} 
