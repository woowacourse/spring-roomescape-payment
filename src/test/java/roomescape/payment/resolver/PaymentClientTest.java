package roomescape.payment.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.client.RestClient.RequestBodySpec;
import static org.springframework.web.client.RestClient.RequestBodyUriSpec;
import static org.springframework.web.client.RestClient.ResponseSpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.payment.domain.vo.PaymentInfo;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.exception.PaymentApiException;

@ExtendWith(MockitoExtension.class)
class PaymentClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentClient paymentClient;

    @Test
    @DisplayName("정상 결제 응답 반환한다")
    void confirmPayment() {
        // given
        PaymentRequest request = new PaymentRequest("paymentKey123", 1000, "orderId123", "paymentType");
        PaymentInfo expectedResponse = new PaymentInfo("paymentKey123", 1000);

        RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
        RequestBodySpec bodySpec = mock(RequestBodySpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);
        // when
        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("/v1/payments/confirm")).thenReturn(bodySpec);
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PaymentInfo.class)).thenReturn(expectedResponse);

        PaymentInfo result = paymentClient.confirmPayment(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("UNAUTHORIZED 예외 발생 시 RuntimeException를 던진다")
    void confirmPayment_throwsRuntimeException_whenUnauthorized() throws Exception {
        // Given
        PaymentRequest request = new PaymentRequest("invalidKey", 1000, "orderId123", "paymentType");
        String errorResponse = "{\"message\":\"인증 실패\"}";

        RestClientResponseException exception = new RestClientResponseException(
                "Unauthorized",
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                null,
                errorResponse.getBytes(),
                null
        );

        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.get("message")).thenReturn(new TextNode("인증 실패"));
        when(objectMapper.readTree(errorResponse)).thenReturn(mockJsonNode);

        RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
        RequestBodySpec bodySpec = mock(RequestBodySpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("/v1/payments/confirm")).thenReturn(bodySpec);
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PaymentInfo.class)).thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("결제 확인에 실패했습니다.")
                .hasMessageContaining("인증 실패");
    }

    @Test
    @DisplayName("기타 API 예외 발생 시, PaymentApiException 을 던진다")
    void confirmPayment_throwsPaymentApiException_whenOtherError() throws Exception{
        // Given
        PaymentRequest request = new PaymentRequest("paymentKey123", 1000, "orderId123", "paymentType");
        String errorResponse = "{ \"code\":\"BAD_REQUEST\", \"message\":\"잘못된 요청\"}";

        RestClientResponseException exception = new RestClientResponseException(
                "Bad Request",
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                errorResponse.getBytes(),
                null
        );

        RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
        RequestBodySpec bodySpec = mock(RequestBodySpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.get("message")).thenReturn(new TextNode("잘못된 요청"));
        when(objectMapper.readTree(errorResponse)).thenReturn(mockJsonNode);

        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("/v1/payments/confirm")).thenReturn(bodySpec);
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PaymentInfo.class)).thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(PaymentApiException.class)
                .hasMessageContaining("결제 Api가 실패하였습니다.")
                .hasMessageContaining("잘못된 요청")
                .hasMessageContaining("BAD_REQUEST");
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 RuntimeException 던진다")
    void confirmPayment_throwsRuntimeException_whenJsonParsingFails() throws Exception {
        // Given
        PaymentRequest request = new PaymentRequest("paymentKey123", 1000, "orderId123", "paymentType");
        String invalidJsonResponse = "invalid json";

        RestClientResponseException exception = new RestClientResponseException(
                "Bad Request",
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                invalidJsonResponse.getBytes(),
                null
        );

        RequestBodyUriSpec uriSpec = mock(RequestBodyUriSpec.class);
        RequestBodySpec bodySpec = mock(RequestBodySpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(objectMapper.readTree(invalidJsonResponse)).thenThrow(new JsonProcessingException("JSON 파싱 오류") {});

        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("/v1/payments/confirm")).thenReturn(bodySpec);
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PaymentInfo.class)).thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("파싱에 실패했습니다.");
    }
}
