package roomescape.infrastructure.thirdparty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.application.exception.PaymentException;
import roomescape.infrastructure.thirdparty.dto.PaymentConfirmResponse;
import roomescape.infrastructure.thirdparty.dto.TossErrorResponse;
import roomescape.presentation.dto.request.PaymentProcessRequest;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@RestClientTest(PaymentRestClient.class)
@TestPropertySource(properties = {
        "toss.payment.api.base-url=https://test-api.tosspayments.com",
        "toss.payment.api.key=test-key"
})
class PaymentRestClientTest {

    private static final String TEST_URL = "https://test-api.tosspayments.com/v1/payments/confirm";

    @Autowired
    private PaymentRestClient client;

    @Autowired
    private MockRestServiceServer server;

    @MockitoBean
    private AuthHeaderGenerator authHeaderGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setHeaders() {
        given(authHeaderGenerator.generateBasicAuthHeader("test-key"))
                .willReturn("Basic " + Base64.encodeBase64String("test-key:".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void 결제가_승인되면_승인응답을_반환한다() throws JsonProcessingException {
        PaymentProcessRequest request = new PaymentProcessRequest("paymentKey", "orderId", "1000");
        PaymentConfirmResponse response = new PaymentConfirmResponse("paymentKey", "orderId");

        server.expect(requestTo(TEST_URL))
                .andRespond(withSuccess(
                        objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON
                ));

        assertThat(client.getPaymentResponse(request)).isEqualTo(response);
    }

    @Test
    void 클라이언트_오류가_발생하면_예외를_반환한다() throws JsonProcessingException {
        PaymentProcessRequest request = new PaymentProcessRequest("paymentKey", "orderId", "1000");
        TossErrorResponse response = new TossErrorResponse("INVALID_REQUEST", "잘못된 요청입니다.");

        server.expect(requestTo(TEST_URL))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response))
                );

        assertThatThrownBy(() -> client.getPaymentResponse(request))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    void 서버_오류가_발생하면_예외를_반환한다() throws JsonProcessingException {
        PaymentProcessRequest request = new PaymentProcessRequest("paymentKey", "orderId", "1000");
        TossErrorResponse response = new TossErrorResponse("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", "결제가 완료되지 않았어요. 다시 시도해주세요.");

        server.expect(requestTo(TEST_URL))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(response))
                );

        assertThatThrownBy(() -> client.getPaymentResponse(request))
                .isInstanceOf(PaymentException.class);
    }
}
