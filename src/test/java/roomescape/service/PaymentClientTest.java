package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.exception.payment.PaymentConfirmException;
import roomescape.service.payment.PaymentClient;
import roomescape.service.payment.dto.PaymentConfirmInput;

public class PaymentClientTest {
    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    private static final String CONFIRM_URL = "/confirm";
    private final RestClient.Builder builder = RestClient.builder()
            .baseUrl(BASE_URL);

    private final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();
    private final PaymentClient paymentClient = new PaymentClient(builder.build());

    @BeforeEach
    void setUp() {
        mockServer.reset();
    }

    @Test
    void 토스페이먼츠에_결제_승인_요청을_보내고_결제_성공_응답을_받을_수_있다() {
        mockServer.expect(requestTo(BASE_URL + CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());
        PaymentConfirmInput input = new PaymentConfirmInput("orderId", 1000, "mock_secret_key");

        assertThatCode(() -> paymentClient.confirmPayment(input))
                .doesNotThrowAnyException();
    }

    @Test
    void 토스페이먼츠에_결제_승인_요청을_보내고_걸제_실패_응답을_받을_수_있다() {
        String expectedBody = """
                {
                  "code": "PROVIDER_ERROR",
                  "message": "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                }
                """;
        mockServer.expect(requestTo(BASE_URL + CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(expectedBody)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        PaymentConfirmInput input = new PaymentConfirmInput("orderId", 1000, "mock_secret_key");

        assertThatCode(() -> paymentClient.confirmPayment(input))
                .isInstanceOf(PaymentConfirmException.class)
                .hasMessage("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
