package roomescape.reservation.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.exception.PaymentClientException;

@ExtendWith(MockitoExtension.class)
class PaymentErrorHandlerTest {

    @Mock
    private ClientHttpResponse clientHttpResponse;

    private PaymentErrorHandler paymentErrorHandler = new PaymentErrorHandler();

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 402, 403, 404, 499})
    void _4xx_에러를_핸들링_한다(int statusCode) throws IOException {
        // given
        when(clientHttpResponse.getStatusCode())
                .thenReturn(HttpStatusCode.valueOf(statusCode));

        PaymentErrorHandler errorHandler = new PaymentErrorHandler();

        // when
        boolean result = errorHandler.hasError(clientHttpResponse);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 501, 502, 503, 504, 599})
    void _4xx_외에_에러는_핸들링_하지_않는다(int statusCode) throws IOException {
        // given
        when(clientHttpResponse.getStatusCode())
                .thenReturn(HttpStatusCode.valueOf(statusCode));

        // when
        boolean result = paymentErrorHandler.hasError(clientHttpResponse);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 예외_응답에서_메시지만_추출하여_던진다() throws IOException {
        // given
        String message = "요청이 올바르지 않습니다";
        Map<String, String> params = Map.of(
                "code", "INVALID_REQUEST",
                "message", message
        );

        ByteArrayInputStream inputStream = new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(params));
        when(clientHttpResponse.getBody()).thenReturn(inputStream);

        //  when then
        assertThatCode(() -> paymentErrorHandler.handleError(null, null, clientHttpResponse)).isInstanceOf(
                PaymentClientException.class).hasMessage(message);
    }
}
