package roomescape.domain.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

class PaymentApiResponseErrorHandlerTest {
    private final PaymentApiResponseErrorHandler handler = new PaymentApiResponseErrorHandler(
            new PaymentErrorParser(new ObjectMapper()));

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
                .filter(paymentApiErrorCode -> !paymentApiErrorCode.isNeedToHide())
                .map(Arguments::of);
    }

    public static Stream<Arguments> handleErrorNeedToHideMessageParameters() {
        return Arrays.stream(PaymentApiErrorCode.values())
                .filter(PaymentApiErrorCode::isNeedToHide)
                .map(Arguments::of);
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
    @DisplayName("API가 메세지를 그대로 보내도 되는 알려진 에러를 응답했을 때 적절한 예외가 발생하는지 확인")
    void handleError(PaymentApiErrorCode knownErrorCode) {
        String message = "message";
        String errorJson = """
                {
                  "code": "%s",
                  "message": "%s"
                }"""
                .formatted(knownErrorCode, message);

        ClientHttpResponse response = getFakeHttpResponse(errorJson);
        Assertions.assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(ApiCallException.class)
                .hasMessage(message);
    }

    private static ClientHttpResponse getFakeHttpResponse(String errorJson) {
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

    @ParameterizedTest
    @MethodSource("handleErrorNeedToHideMessageParameters")
    @DisplayName("API가 메세지를 그대로 보내면 안되는 에러를 응답했을 때 적절한 예외가 발생하는지 확인")
    void handleErrorNeedToHideMessage(PaymentApiErrorCode knownErrorCode) {
        String message = "message";
        String errorJson = """
                {
                  "code": "%s",
                  "message": "%s"
                }"""
                .formatted(knownErrorCode, message);

        ClientHttpResponse response = getFakeHttpResponse(errorJson);
        Assertions.assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(ApiCallException.class)
                .hasMessage("결제를 진행할 수 없습니다. 고객 센터로 문의해 주세요.");
    }

    @Test
    @DisplayName("API가 알수 없는 에러를 응답했을 때 적절한 예외가 발생하는지 확인")
    void handleUnknownError() {
        String errorJson = """
                {
                  "code": "code_e63022aece25",
                  "message": "message_799dc46c7cf3"
                }""";
        ClientHttpResponse response = getFakeHttpResponse(errorJson);
        Assertions.assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(ApiCallException.class)
                .hasMessage("결제를 진행할 수 없습니다. 고객 센터로 문의해 주세요.");
    }
}
