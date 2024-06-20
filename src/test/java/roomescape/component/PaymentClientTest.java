package roomescape.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.config.payment.TossPaymentConfigProperties;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.dto.payment.TossPaymentErrorResponse;
import roomescape.exception.RoomescapeException;
import roomescape.exception.TossPaymentErrorCode;

@SpringBootTest
class PaymentClientTest {

    @Autowired
    private TossPaymentConfigProperties tossProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestClient.Builder tossRestClientBuilder;

    private TossPaymentClient paymentClient;

    private MockRestServiceServer mockServer;

    private String uri;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(tossRestClientBuilder).build();
        paymentClient = new TossPaymentClient(tossProperties, tossRestClientBuilder);
        uri = tossProperties.baseUri() + tossProperties.confirmUri();
    }

    @Test
    @DisplayName("결제 승인에 성공한다.")
    void confirm() throws JsonProcessingException {
        var request = new PaymentConfirmRequest("paymentKey", "orderId", 1000L);
        var response = new PaymentConfirmResponse("paymentKey", "orderId", 1000L);
        var responseString = objectMapper.writeValueAsString(response);

        mockServer.expect(requestTo(uri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(request)))
                .andRespond(withSuccess(responseString, MediaType.APPLICATION_JSON));

        assertThat(paymentClient.confirm(request)).isEqualTo(response);

        mockServer.verify();
    }

    @ParameterizedTest
    @EnumSource(TossPaymentErrorCode.class)
    @DisplayName("결제 승인 오류 시 예외가 발생한다.")
    void confirmException(TossPaymentErrorCode errorCode) throws IOException {
        var request = new PaymentConfirmRequest("paymentKey", "orderId", 1000L);
        var response = objectMapper.writeValueAsString(
                new TossPaymentErrorResponse(errorCode.name(), errorCode.message()));

        mockServer.expect(requestTo(uri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(errorCode.httpStatusCode())
                        .body(response)
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> paymentClient.confirm(request))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(errorCode.message());

        mockServer.verify();
    }
}
