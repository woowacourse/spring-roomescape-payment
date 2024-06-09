package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.config.PaymentClientConfig;
import roomescape.exception.PaymentClientException;
import roomescape.exception.PaymentServerException;
import roomescape.service.dto.request.PaymentRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest
@Import(PaymentClientConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TossPaymentClientTest {

    private final PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", BigDecimal.TEN);
    private final MockRestServiceServer server;
    private final TossPaymentClient tossPaymentClient;
    @Autowired
    private ObjectMapper objectMapper;

    public TossPaymentClientTest(@Qualifier("restClientBuilder") RestClient.Builder restClientBuilder) {
        this.server = MockRestServiceServer.bindTo(restClientBuilder).build();
        this.tossPaymentClient = new TossPaymentClient(restClientBuilder.build());
    }

    @AfterEach
    void tearDown() {
        server.reset();
    }

    @Test
    @DisplayName("Timeout 등의 IOException이 발생하면 PaymentServerException을 던진다.")
    void handleIoException() {
        server.expect(anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withException(new IOException("timeout")));

        assertThatThrownBy(() -> tossPaymentClient.pay(paymentRequest))
                .isExactlyInstanceOf(PaymentServerException.class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideForPaymentClientException")
    @DisplayName("toss 에러 중 클라이언트 관련 에러는 PaymentClientException을 던진다.")
    void handlePaymentClientException(String errorCode, String message, HttpStatus httpStatus) throws Exception {
        String response = objectMapper.writeValueAsString(new TossErrorResponse(errorCode, message));

        server.expect(anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(httpStatus).body(response));

        assertThatThrownBy(() -> tossPaymentClient.pay(paymentRequest))
                .isExactlyInstanceOf(PaymentClientException.class)
                .hasMessageContaining(message)
                .extracting("statusCode")
                .isEqualTo(httpStatus);
    }

    private Stream<Arguments> provideForPaymentClientException() {
        return Stream.of(
                Arguments.of("NOT_FOUND_PAYMENT_SESSION", "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
                Arguments.of("REJECT_CARD_COMPANY", "결제 승인이 거절되었습니다.", HttpStatus.FORBIDDEN),
                Arguments.of("ALREADY_PROCESSED_PAYMENT", "이미 처리된 결제 입니다.", HttpStatus.BAD_REQUEST)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideForPaymentServerException")
    @DisplayName("toss 에러 중 서버 관련 에러는 PaymentServerException을 던진다.")
    void handlePaymentServerException(String errorCode, String message, HttpStatus httpStatus) throws Exception {
        String response = objectMapper.writeValueAsString(new TossErrorResponse(errorCode, message));

        server.expect(anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(httpStatus).body(response));

        assertThatThrownBy(() -> tossPaymentClient.pay(paymentRequest))
                .isExactlyInstanceOf(PaymentServerException.class)
                .hasMessageContaining(message);
    }

    private Stream<Arguments> provideForPaymentServerException() {
        return Stream.of(
                Arguments.of("UNAUTHORIZED_KEY", "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.", HttpStatus.UNAUTHORIZED),
                Arguments.of("FORBIDDEN_REQUEST", "허용되지 않은 요청입니다.", HttpStatus.FORBIDDEN),
                Arguments.of("INCORRECT_BASIC_AUTH_FORMAT", "잘못된 요청입니다. ':' 를 포함해 인코딩해주세요.", HttpStatus.FORBIDDEN),
                Arguments.of("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", "결제가 완료되지 않았어요. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
                Arguments.of("FAILED_INTERNAL_SYSTEM_PROCESSING", "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
                Arguments.of("UNKNOWN_PAYMENT_ERROR", "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }
}
