package roomescape.payment.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.ViolationException;
import roomescape.payment.application.PaymentServerException;
import roomescape.payment.application.ProductPayRequest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static roomescape.TestFixture.PRODUCT_PAY_REQUEST;

@RestClientTest
@Import(TossRestClientConfiguration.class)
class TossPaymentsClientTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockRestServiceServer mockServer;
    private final TossPaymentsClient paymentsClient;

    public TossPaymentsClientTest(@Qualifier(value = "tossRestClientBuilder") RestClient.Builder builder) {
        this.mockServer = MockRestServiceServer.bindTo(builder).build();
        this.paymentsClient = new TossPaymentsClient(
                new TossPaymentsErrorHandler(objectMapper),
                builder,
                "test",
                "test"
        );
    }

    @Test
    @DisplayName("토스 API 서버의 4xx 응답을 처리할 수 있다.")
    void onStatus4xx() throws Exception {
        // given
        TossPaymentsErrorResponse errorResponse = new TossPaymentsErrorResponse("BAD", "잘못된 요청");
        byte[] expectedBody = objectMapper.writeValueAsBytes(errorResponse);

        mockServer.expect(anything())
                .andRespond(withBadRequest().body(expectedBody));

        ProductPayRequest productPayRequest = PRODUCT_PAY_REQUEST();

        // when & then
        TossPaymentsConfirmRequest request = new TossPaymentsConfirmRequest(productPayRequest);
        assertThatThrownBy(() -> paymentsClient.confirm(request))
                .isInstanceOf(ViolationException.class)
                .hasMessage("잘못된 요청");
    }

    @Test
    @DisplayName("토스 API 서버의 5xx 응답을 처리할 수 있다.")
    void onStatus5xx() throws Exception {
        // given
        TossPaymentsErrorResponse errorResponse = new TossPaymentsErrorResponse("INTERNAL", "내부 서버 오류");
        byte[] expectedBody = objectMapper.writeValueAsBytes(errorResponse);

        mockServer.expect(anything())
                .andRespond(withServerError().body(expectedBody));

        ProductPayRequest productPayRequest = PRODUCT_PAY_REQUEST();

        // when & then
        TossPaymentsConfirmRequest request = new TossPaymentsConfirmRequest(productPayRequest);
        assertThatThrownBy(() -> paymentsClient.confirm(request))
                .isInstanceOf(PaymentServerException.class)
                .hasMessage("내부 서버 오류");
    }
}
