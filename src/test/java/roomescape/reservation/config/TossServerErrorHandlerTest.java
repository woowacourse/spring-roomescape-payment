package roomescape.reservation.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.exception.RoomescapeException;

@ExtendWith(MockitoExtension.class)
class TossServerErrorHandlerTest {

    @Mock
    private ClientHttpResponse clientHttpResponse;

    private final TossServerErrorHandler tossServerErrorHandler = new TossServerErrorHandler();

    @ParameterizedTest
    @ValueSource(ints = {500, 501, 503, 504, 598, 599})
    void _5xx_에러를_핸들링_한다(int statusCode) throws IOException {
        // given
        when(clientHttpResponse.getStatusCode())
                .thenReturn(HttpStatusCode.valueOf(statusCode));

        // when
        boolean result = tossServerErrorHandler.hasError(clientHttpResponse);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {100, 200, 300, 400})
    void _5xx_외에는_핸들링_하지_않는다(int statusCode) throws IOException {
        // given
        when(clientHttpResponse.getStatusCode())
                .thenReturn(HttpStatusCode.valueOf(statusCode));

        // when
        boolean result = tossServerErrorHandler.hasError(clientHttpResponse);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 서버_에러_발생시_RoomescapeException을_던진다() throws IOException {
        // given
        String errorMessage = "서버 에러 발생";
        byte[] responseBody = errorMessage.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(responseBody);

        when(clientHttpResponse.getBody()).thenReturn(inputStream);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        // when & then
        assertThatThrownBy(() -> tossServerErrorHandler.handleError(null, null, clientHttpResponse))
                .isInstanceOf(RoomescapeException.class);
    }
}
