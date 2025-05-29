package roomescape.unit.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.exception.custom.PaymentException;
import roomescape.service.PaymentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PaymentServiceTest {

    public static final String API_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl("https://api.tosspayments.com");

    MockRestServiceServer mockServer = MockRestServiceServer.bindTo(testBuilder).build();

    PaymentService paymentService = new PaymentService(testBuilder.build());

    @BeforeEach
    void setUp() {
        mockServer.reset();
    }

    @Test
    void 결제_api를_기반으로_결제를_승인한다() {
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
        ConfirmPaymentResponse actual = paymentService.confirmPayment(request);

        // then
        ConfirmPaymentResponse expected = new ConfirmPaymentResponse("testPaymentKey", "testOrderId", 1000);
        assertThat(actual).isEqualTo(expected);
        mockServer.verify();
    }

    @Test
    void 결제_api에서_오류가_발생한_경우_예외를_던진다() {
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
        Assertions.assertThatThrownBy(() -> paymentService.confirmPayment(request))
                .isInstanceOf(PaymentException.class);
        mockServer.verify();
    }
}
