package roomescape.component;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import static roomescape.exception.RoomescapeExceptionCode.INTERNAL_SERVER_ERROR;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.exception.RoomescapeException;

class PaymentClientTest {

    private ResponseErrorHandler errorHandler;
    private RestClient.Builder restClient;
    private TossPaymentClient paymentClient;

    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        errorHandler = mock(ResponseErrorHandler.class);
        restClient = RestClient.builder().baseUrl("/confirm");
        paymentClient = new TossPaymentClient(restClient, errorHandler);
        objectMapper = new ObjectMapper();
        mockServer = MockRestServiceServer.bindTo(restClient).build();
    }

    @Test
    @DisplayName("결제 승인에 성공한다.")
    void confirm() throws JsonProcessingException {
        var request = new PaymentConfirmRequest("paymentKey", "orderId", 1000L, 1L);
        var response = new PaymentConfirmResponse("paymentKey", "orderId", 1000L);
        var responseString = objectMapper.writeValueAsString(response);

        mockServer.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseString, MediaType.APPLICATION_JSON));

        assertDoesNotThrow(() -> paymentClient.confirm(request));

        mockServer.verify();
    }

    @Test
    @DisplayName("결제 승인 오류 시 예외가 발생한다.")
    void confirmException() throws IOException {
        var request = new PaymentConfirmRequest("paymentKey", "orderId", 1000L, 1L);

        mockServer.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        when(errorHandler.hasError(any())).thenReturn(true);
        doThrow(new RoomescapeException(INTERNAL_SERVER_ERROR)).when(errorHandler).handleError(any());

        assertThatThrownBy(() -> paymentClient.confirm(request))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(INTERNAL_SERVER_ERROR.message());

        mockServer.verify();
    }
}
