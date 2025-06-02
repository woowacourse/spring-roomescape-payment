package roomescape.unit.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.domain.PaymentClient;
import roomescape.domain.TossErrorCode;
import roomescape.domain.TossPaymentClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.exception.custom.PaymentException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TossPaymentClientTest {

    static final String BASE_URL = "https://api.tosspayments.com";
    static final String API_URL = BASE_URL + "/v1/payments/confirm";

    final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl(BASE_URL);

    MockRestServiceServer mockServer = MockRestServiceServer.bindTo(testBuilder).build();

    PaymentClient paymentClient = new TossPaymentClient(testBuilder.build(), new ObjectMapper());

    @BeforeEach
    void setUp() {
        mockServer.reset();
    }

    @Test
    void 토스_결제_api를_기반으로_결제를_승인한다() {
        // given
        String paymentKey = "testPaymentKey";
        String orderId = "testOrderId";
        int amount = 1000;
        String paymentType = "paymentType";
        ConfirmPaymentRequest request = new ConfirmPaymentRequest(paymentKey, orderId, amount, paymentType);

        String expectedResponse = """
                {
                    "paymentKey": "testPaymentKey",
                    "orderId": "testOrderId",
                    "totalAmount": "1000"
                }
                """;

        mockServer
                .expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // when
        ConfirmPaymentResponse actual = paymentClient.confirmPayment(request);

        // then
        ConfirmPaymentResponse expected = new ConfirmPaymentResponse("testPaymentKey", "testOrderId", 1000);
        assertThat(actual).isEqualTo(expected);
        mockServer.verify();
    }

    @Test
    void 결제_api에서_오류가_발생한_경우_토스_정의_예외_메시지를_던진다() {
        // given
        String paymentKey = "testPaymentKey";
        String orderId = "testOrderId";
        int amount = 1000;
        String paymentType = "paymentType";
        ConfirmPaymentRequest request = new ConfirmPaymentRequest(paymentKey, orderId, amount, paymentType);
        String expectedResponse = """
                {
                    "code": "NOT_FOUND_PAYMENT",
                    "message": "존재하지 않는 결제 입니다."
                }
                """;

        mockServer
                .expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withResourceNotFound()
                        .body(expectedResponse)
                        .contentType(MediaType.APPLICATION_JSON));

        // when // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(PaymentException.class);
        mockServer.verify();
    }

    @ParameterizedTest
    @EnumSource(TossErrorCode.class)
    void 결제_api에서_특정_오류가_발생한_경우_정의된_예외_메시지를_던진다(TossErrorCode tossErrorCode) {
        // given
        String paymentKey = "testPaymentKey";
        String orderId = "testOrderId";
        int amount = 1000;
        String paymentType = "paymentType";
        ConfirmPaymentRequest request = new ConfirmPaymentRequest(paymentKey, orderId, amount, paymentType);
        String expectedResponse = """
                {
                    "code": %s,
                    "message": "에러 메시지"
                }
                """.formatted(tossErrorCode.name());

        mockServer
                .expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withResourceNotFound()
                        .body(expectedResponse)
                        .contentType(MediaType.APPLICATION_JSON));

        // when // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("결제 오류: " + tossErrorCode.name());
        mockServer.verify();
    }
}
