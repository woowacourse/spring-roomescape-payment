package roomescape.reservation.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.global.exception.PaymentClientException;
import roomescape.global.exception.RoomescapeException;

@ExtendWith(MockitoExtension.class)
class TossClientErrorHandlerTest {

    @Mock
    private ClientHttpResponse clientHttpResponse;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TossClientErrorHandler tossClientErrorHandler = new TossClientErrorHandler();

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 404, 498, 499})
    void _4xx_에러를_핸들링_한다(int statusCode) throws IOException {
        // given
        when(clientHttpResponse.getStatusCode())
                .thenReturn(HttpStatusCode.valueOf(statusCode));

        // when
        boolean result = tossClientErrorHandler.hasError(clientHttpResponse);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 300, 301, 500, 501})
    void _4xx_외에는_핸들링_하지_않는다(int statusCode) throws IOException {
        // given
        when(clientHttpResponse.getStatusCode())
                .thenReturn(HttpStatusCode.valueOf(statusCode));

        // when
        boolean result = tossClientErrorHandler.hasError(clientHttpResponse);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 외부_예외_응답의_메시지만_추출해_예외를_던진다() throws IOException {
        // given
        String message = "요청이 올바르지 않습니다";
        Map<String, String> params = Map.of(
                "code", "INVALID_REQUEST",
                "message", message
        );

        ByteArrayInputStream inputStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(params));
        when(clientHttpResponse.getBody()).thenReturn(inputStream);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        //  when then
        assertThatCode(() -> tossClientErrorHandler.handleError(null, null, clientHttpResponse))
                .isInstanceOf(PaymentClientException.class)
                .hasMessage(message);
    }

    @Test
    void JSON_파싱_에러시_RoomescapeException을_던진다() throws IOException {
        // given
        String invalidJson = "{ invalid json }";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidJson.getBytes());
        when(clientHttpResponse.getBody()).thenReturn(inputStream);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatusCode.valueOf(400));

        // when & then
        assertThatThrownBy(() ->
                tossClientErrorHandler.handleError(null, null, clientHttpResponse))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    void IOException_발생시_RoomescapeException을_던진다() throws IOException {
        // given
        when(clientHttpResponse.getBody()).thenThrow(new IOException("네트워크 에러"));
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatusCode.valueOf(400));

        // when & then
        assertThatThrownBy(() ->
                tossClientErrorHandler.handleError(null, null, clientHttpResponse))
                .isInstanceOf(RoomescapeException.class);
    }
}
