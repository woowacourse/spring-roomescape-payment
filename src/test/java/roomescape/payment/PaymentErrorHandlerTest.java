package roomescape.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.exception.BadRequestException;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InternalServerException;
import roomescape.exception.UnauthorizedException;


class PaymentErrorHandlerTest {
    private PaymentErrorHandler paymentErrorHandler;
    private ClientHttpResponse response;
    private URI testUri;

    @BeforeEach
    void setUp() {
        paymentErrorHandler = new PaymentErrorHandler();
        response = mock(ClientHttpResponse.class);
        testUri = URI.create("https://api.tosspayments.com/v1/payments");
    }

    @Test
    @DisplayName("4xx 클라이언트 오류가 있는지 확인")
    void hasError_4xxClientError_returnsTrue() throws IOException {
        // given
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        // when & then
        assertThat(paymentErrorHandler.hasError(response)).isTrue();
    }

    @Test
    @DisplayName("5xx 서버 오류가 있는지 확인")
    void hasError_5xxServerError_returnsTrue() throws IOException {
        // given
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        // when & then
        assertThat(paymentErrorHandler.hasError(response)).isTrue();
    }

    @Test
    @DisplayName("2xx 성공 응답은 오류가 없음")
    void hasError_2xxSuccess_returnsFalse() throws IOException {
        // given
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        // when & then
        assertThat(paymentErrorHandler.hasError(response)).isFalse();
    }

    @Test
    @DisplayName("400 Bad Request 오류 처리")
    void handleError_badRequest_throwsBadRequestException() throws IOException {
        // given
        String errorJson = "{\"code\":\"INVALID_REQUEST\",\"message\":\"잘못된 요청입니다.\"}";
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes()));

        // when & then
        assertThatThrownBy(() ->
                paymentErrorHandler.handleError(testUri, HttpMethod.POST, response)
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("잘못된 요청입니다.");
    }

    @Test
    @DisplayName("401 Unauthorized 오류 처리")
    void handleError_unauthorized_throwsUnauthorizedException() throws IOException {
        // given
        String errorJson = "{\"code\":\"UNAUTHORIZED\",\"message\":\"인증되지 않은 요청입니다.\"}";
        when(response.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes()));

        // when & then
        assertThatThrownBy(() ->
                paymentErrorHandler.handleError(testUri, HttpMethod.POST, response)
        )
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("인증되지 않은 요청입니다.");
    }

    @Test
    @DisplayName("403 Forbidden 오류 처리")
    void handleError_forbidden_throwsForbiddenException() throws IOException {
        // given
        String errorJson = "{\"code\":\"FORBIDDEN\",\"message\":\"권한이 없습니다.\"}";
        when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes()));

        // when & then
        assertThatThrownBy(() ->
                paymentErrorHandler.handleError(testUri, HttpMethod.POST, response)
        )
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("권한이 없습니다.");
    }


    @DisplayName("토스 커스텀 에러 코드 테스트")
    @Nested
    class TossCustomErrorCodeTest {

        @Test
        @DisplayName("500 PROVIDER_ERROR 오류 처리")
        void handleError_provider_error_Exception() throws IOException {
            // given
            String errorJson = "{\"code\":\"PROVIDER_ERROR\",\"message\":\"토스 에러 메세지.\"}";
            when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
            when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes()));

            // when & then
            assertThatThrownBy(() ->
                    paymentErrorHandler.handleError(testUri, HttpMethod.POST, response)
            )
                    .isInstanceOf(InternalServerException.class)
                    .hasMessageContaining("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요");
        }

        @Test
        @DisplayName("500 INVALID_API_KEY 오류 처리")
        void handleError_invalid_api_key_Exception() throws IOException {
            // given
            String errorJson = "{\"code\":\"INVALID_API_KEY\",\"message\":\"토스 에러 메세지.\"}";
            when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
            when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes()));

            // when & then
            assertThatThrownBy(() ->
                    paymentErrorHandler.handleError(testUri, HttpMethod.POST, response)
            )
                    .isInstanceOf(InternalServerException.class)
                    .hasMessageContaining("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요");
        }

        @Test
        @DisplayName("500 INVALID_AUTHORIZE_AUTH 오류 처리")
        void handleError_invalid_authorize_auth_Exception() throws IOException {
            // given
            String errorJson = "{\"code\":\"INVALID_AUTHORIZE_AUTH\",\"message\":\"토스 에러 메세지.\"}";
            when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
            when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes()));

            // when & then
            assertThatThrownBy(() ->
                    paymentErrorHandler.handleError(testUri, HttpMethod.POST, response)
            )
                    .isInstanceOf(InternalServerException.class)
                    .hasMessageContaining("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요");
        }

        @Test
        @DisplayName("500 UNAUTHORIZED_KEY 오류 처리")
        void handleError_unauthorized_key_Exception() throws IOException {
            // given
            String errorJson = "{\"code\":\"UNAUTHORIZED_KEY\",\"message\":\"토스 에러 메세지.\"}";
            when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
            when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes()));

            // when & then
            assertThatThrownBy(() ->
                    paymentErrorHandler.handleError(testUri, HttpMethod.POST, response)
            )
                    .isInstanceOf(InternalServerException.class)
                    .hasMessageContaining("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요");
        }

        @Test
        @DisplayName("500 INCORRECT_BASIC_AUTH_FORMAT 오류 처리")
        void handleError_incorrect_basic_auth_format_Exception() throws IOException {
            // given
            String errorJson = "{\"code\":\"INCORRECT_BASIC_AUTH_FORMAT\",\"message\":\"토스 에러 메세지.\"}";
            when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
            when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes()));

            // when & then
            assertThatThrownBy(() ->
                    paymentErrorHandler.handleError(testUri, HttpMethod.POST, response)
            )
                    .isInstanceOf(InternalServerException.class)
                    .hasMessageContaining("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요");
        }

        @Test
        @DisplayName("400 INVALID_UNREGISTERED_SUBMALL 오류 처리")
        void handleError_invalid_unregistered_submall_Exception() throws IOException {
            // given
            String errorJson = "{\"code\":\"INVALID_UNREGISTERED_SUBMALL\",\"message\":\"토스 에러 메세지.\"}";
            when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
            when(response.getBody()).thenReturn(new ByteArrayInputStream(errorJson.getBytes()));

            // when & then
            assertThatThrownBy(() ->
                    paymentErrorHandler.handleError(testUri, HttpMethod.POST, response)
            )
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("안심클릭이나 ISP 결제가 필요합니다");
        }
    }
}