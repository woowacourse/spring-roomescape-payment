package roomescape.domain.payment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;

class PaymentApiResponseErrorHandlerTest {
    private final PaymentApiResponseErrorHandler handler = new PaymentApiResponseErrorHandler(
            new PaymentErrorParser(
                    new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            )
    );

    public static Stream<Arguments> hasErrorParameter() {
        return Stream.of(
                Arguments.of(200, false),
                Arguments.of(400, true),
                Arguments.of(401, true),
                Arguments.of(403, true),
                Arguments.of(404, true),
                Arguments.of(500, true)
        );
    }

    public static Stream<Arguments> handleErrorParameters() {
        return Arrays.stream(PaymentApiErrorCode.values())
                .map(code -> {
                    PaymentApiError paymentServerSendError = new PaymentApiError(code, "Payment server send Error");
                    return Arguments.of(code, paymentServerSendError.mapToExceptionType());
                });
    }

    @ParameterizedTest
    @MethodSource("hasErrorParameter")
    @DisplayName("API 응답 상태 코드에 따라 에러 여부를 잘 판단하는지 확인")
    void hasError(int statusCode, boolean expected) throws IOException {
        ClientHttpResponse fakeHttpResponse = getFakeHttpResponse(statusCode);
        boolean hasError = handler.hasError(fakeHttpResponse);

        Assertions.assertThat(hasError)
                .isEqualTo(expected);
    }

    private ClientHttpResponse getFakeHttpResponse(int statusCode) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatusCode getStatusCode() {
                return HttpStatusCode.valueOf(statusCode);
            }

            @Override
            public String getStatusText() {
                return null;
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() {
                return null;
            }

            @Override
            public HttpHeaders getHeaders() {
                return null;
            }
        };
    }

    @ParameterizedTest
    @MethodSource("handleErrorParameters")
    @DisplayName("에러 발생 시 적절한 예외를 발생시키는지 확인")
    void handleError(PaymentApiErrorCode code, ExceptionType expectedExceptionType) {
        String errorJson = """
                {
                  "code": "%s",
                  "message": "message_799dc46c7cf3"
                }""".formatted(code);
        ClientHttpResponse response = getFakeHttpResponse(errorJson);
        Assertions.assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(expectedExceptionType.getMessage());
    }

    private ClientHttpResponse getFakeHttpResponse(String errorJson) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatusCode getStatusCode() {
                return null;
            }

            @Override
            public String getStatusText() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(errorJson.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                return null;
            }
        };
    }
}
