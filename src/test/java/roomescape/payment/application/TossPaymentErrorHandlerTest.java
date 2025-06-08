package roomescape.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.common.exception.impl.DeserializationException;
import roomescape.common.exception.impl.TossPaymentErrorException;
import roomescape.payment.application.dto.TossErrorResponse;

class TossPaymentErrorHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TossPaymentErrorHandler tossPaymentErrorHandler = new TossPaymentErrorHandler(objectMapper);

    @Test
    void 응답이_오류일_경우_에러를_발생시킨다() throws Exception {
        // given
        TossErrorResponse error = new TossErrorResponse("INVALID_PAYMENT", "결제가 유효하지 않습니다.");
        String errorJson = objectMapper.writeValueAsString(error);

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes(StandardCharsets.UTF_8)));

        // when & then
        assertThatThrownBy(() -> tossPaymentErrorHandler.handleError(new URI("http://localhost"), HttpMethod.POST, response))
            .isInstanceOf(TossPaymentErrorException.class)
            .satisfies(e -> {
                TossPaymentErrorException ex = (TossPaymentErrorException) e;
                assertAll(
                    () -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
                    () -> assertThat(ex.getCode()).isEqualTo("INVALID_PAYMENT"),
                    () -> assertThat(ex.getMessage()).isEqualTo("결제가 유효하지 않습니다.")
                );
            });
    }

    @Test
    void 응답_파싱에_실패하면_에러를_발생시킨다() throws Exception {
        // given
        String invalidJson = "this is not json";

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8)));

        // when & then
        assertThatThrownBy(() -> tossPaymentErrorHandler.handleError(new URI("http://localhost"), HttpMethod.GET, response))
            .isInstanceOf(DeserializationException.class)
            .hasMessage("Error parsing Toss error");
    }
}
