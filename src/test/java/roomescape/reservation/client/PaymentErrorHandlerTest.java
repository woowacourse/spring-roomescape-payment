package roomescape.reservation.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

@ExtendWith(MockitoExtension.class)
class PaymentErrorHandlerTest {

    @Mock
    private ClientHttpResponse clientHttpResponse;

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

        PaymentErrorHandler errorHandler = new PaymentErrorHandler();

        // when
        boolean result = errorHandler.hasError(clientHttpResponse);

        // then
        assertThat(result).isFalse();
    }
}
