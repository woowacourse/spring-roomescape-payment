package roomescape.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.dto.payment.TossPaymentErrorResponse;
import roomescape.exception.RoomescapeException;
import roomescape.exception.TossPaymentErrorCode;

@RestClientTest(TossPaymentClient.class)
class PaymentClientTest {

    @Value("${payment.toss.confirm-uri}")
    private String confirmUri;

    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private TossPaymentClient paymentClient;
    @MockBean
    private ResponseErrorHandler errorHandler;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("결제 승인에 성공한다.")
    void confirm() throws JsonProcessingException {
        var request = new PaymentConfirmRequest("paymentKey", "orderId", 1000L, 1L);
        var response = new PaymentConfirmResponse("paymentKey", "orderId", 1000L);
        var responseString = objectMapper.writeValueAsString(response);

        mockServer
                .expect(requestTo(confirmUri))
                .andExpect(content().json(objectMapper.writeValueAsString(request)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseString, MediaType.APPLICATION_JSON));

        assertThat(paymentClient.confirm(request)).isEqualTo(response);
        mockServer.verify();
    }

    @ParameterizedTest
    @EnumSource(TossPaymentErrorCode.class)
    @DisplayName("결제 승인 오류 시 예외가 발생한다.")
    void confirmException(TossPaymentErrorCode errorCode) throws IOException {
        var request = new PaymentConfirmRequest("paymentKey", "orderId", 1000L, 1L);
        var response = objectMapper.writeValueAsString(
                new TossPaymentErrorResponse(errorCode.name(), errorCode.message()));

        mockServer.expect(requestTo(confirmUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest().body(response));

        when(errorHandler.hasError(any())).thenThrow(new RoomescapeException(errorCode));

        assertThatThrownBy(() -> paymentClient.confirm(request))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(errorCode.message());

        mockServer.verify();

    }
}
