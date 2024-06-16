package roomescape.infrastructure.payment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.payment.PaymentFailException;
import roomescape.infrastructure.payment.PaymentProperties.PaymentProperty;
import roomescape.infrastructure.payment.toss.TossPaymentClient;
import roomescape.infrastructure.payment.toss.TossResponseErrorHandler;

class TossPaymentClientTest {

    private MockRestServiceServer mockRestServiceServer;
    private PaymentClient tossPaymentClient;

    @BeforeEach
    void setUp() {
        PaymentProperty property = new PaymentProperty();
        property.setSecretKey("secretKey");
        property.setPassword("password");
        property.setConnectionTimeoutSeconds(3);
        property.setReadTimeoutSeconds(5);

        PaymentProperties properties = new PaymentProperties(Map.of("toss", property));

        PaymentRestClientBuilder paymentRestClientBuilder = new PaymentRestClientBuilder(properties);
        RestClient.Builder builder = paymentRestClientBuilder.generate("toss");

        mockRestServiceServer = MockRestServiceServer.bindTo(builder).build();
        tossPaymentClient = new TossPaymentClient(builder.build(), new TossResponseErrorHandler());
    }

    @DisplayName("응답의 에러 코드가 정의해놓은 에러 코드에 포함되는 경우 에러 메시지와 상태코드를 매핑해서 반환한다.")
    @Test
    void throw_exception_and_message_when_error_code_is_defined() {
        PaymentRequest paymentRequest = new PaymentRequest(BigDecimal.valueOf(1000), "orderId", "paymentKey");
        String errorResponse = """
                {
                  "code": "FAILED_INTERNAL_SYSTEM_PROCESSING",
                  "message": "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."
                }
                """;

        mockRestServiceServer.expect(requestTo("/v1/payments/confirm"))
                .andRespond(withServerError()
                        .body(errorResponse)
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentRequest))
                .isInstanceOf(PaymentFailException.class)
                .hasMessage("결제에 실패했습니다. 잠시 후 다시 시도해주세요.");
    }

    @DisplayName("응답의 에러 코드가 정의한 에러 코드에 포함되지 않을 경우 적절한 에러 메시지와 상태코드 500을 반환한다.")
    @Test
    void throw_exception_with_500_status_code_and_default_message() {
        PaymentRequest paymentRequest = new PaymentRequest(BigDecimal.valueOf(1000), "orderId", "paymentKey");
        String errorResponse = """
                {
                  "code": "UNAUTHORIZED_KEY",
                  "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                }
                """;

        mockRestServiceServer.expect(requestTo("/v1/payments/confirm"))
                .andRespond(withBadRequest()
                        .body(errorResponse)
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentRequest))
                .isInstanceOf(PaymentFailException.class)
                .hasMessage("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
