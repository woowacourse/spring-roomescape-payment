package roomescape.payment.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.common.exception.impl.DeserializationException;
import roomescape.common.exception.impl.ExternalApiException;
import roomescape.payment.application.dto.TossErrorResponse;

class TossPaymentErrorHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TossPaymentErrorHandler tossPaymentErrorHandler = new TossPaymentErrorHandler(
        objectMapper);

    @ParameterizedTest
    @MethodSource("tossErrorCodesTreatedAsClientError")
    void 응답이_클라이언트_오류로_간주되는_경우_그대로_에러를_발생시킨다(String errorCode) throws Exception {
        // given
        TossErrorResponse error = new TossErrorResponse(errorCode, "Toss 서버 오류");
        String errorJson = objectMapper.writeValueAsString(error);

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getBody()).thenReturn(
            new ByteArrayInputStream(errorJson.getBytes(StandardCharsets.UTF_8)));

        // when & then
        assertThatThrownBy(
            () -> tossPaymentErrorHandler.handleError(new URI("http://localhost"), HttpMethod.POST,
                response))
            .isInstanceOf(ExternalApiException.class)
            .satisfies(e -> {
                ExternalApiException ex = (ExternalApiException) e;
                assertAll(
                    () -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
                    () -> assertThat(ex.getMessage()).isEqualTo("Toss 서버 오류")
                );
            });
    }

    @Test
    void 응답이_서버_내부_오류로_간주되는_경우_변환된_에러를_발생시킨다() throws Exception {
        // given
        TossErrorResponse error = new TossErrorResponse("INVALID_AUTHORIZE_AUTH",
            "유효하지 않은 인증 방식입니다");
        String errorJson = objectMapper.writeValueAsString(error);

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getBody()).thenReturn(
            new ByteArrayInputStream(errorJson.getBytes(StandardCharsets.UTF_8)));

        // when & then
        assertThatThrownBy(
            () -> tossPaymentErrorHandler.handleError(new URI("http://localhost"), HttpMethod.POST,
                response))
            .isInstanceOf(ExternalApiException.class)
            .satisfies(e -> {
                ExternalApiException ex = (ExternalApiException) e;
                assertAll(
                    () -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                    () -> assertThat(ex.getMessage()).isEqualTo("결제가 실패했습니다. 관리자 문의가 필요합니다.")
                );
            });
    }

    private static Stream<String> tossErrorCodesTreatedAsClientError() {
        return Stream.of(TossPaymentErrorHandler.TossErrorCodesTreatedAsClientError.values())
            .map(Enum::name);
    }

    @Test
    void 응답_파싱에_실패하면_에러를_발생시킨다() throws Exception {
        // given
        String invalidJson = "this is not json";

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(response.getBody()).thenReturn(
            new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8)));

        // when & then
        assertThatThrownBy(
            () -> tossPaymentErrorHandler.handleError(new URI("http://localhost"), HttpMethod.GET,
                response))
            .isInstanceOf(DeserializationException.class)
            .hasMessage("Error parsing Toss error");
    }
}
