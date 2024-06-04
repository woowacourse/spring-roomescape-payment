package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.ViolationException;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.NewPayment;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.exception.PaymentServerException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TossPaymentsClientTest {
    public static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    public static final String CONFIRM_URL = BASE_URL + "/confirm";

    private final RestClient.Builder testRestClientBuilder = RestClient.builder()
            .baseUrl(BASE_URL);
    private final MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(testRestClientBuilder)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PaymentClientErrorHandler errorHandler = new PaymentClientErrorHandler(objectMapper);
    private final PaymentClient tossPaymentClient = new TossPaymentsClient(testRestClientBuilder, errorHandler);

    @BeforeEach
    void setUp() {
        mockRestServiceServer.reset();
    }

    @Test
    @DisplayName("토스 결제 승인 API에서 승인 응답을 받는다.")
    void confirm() throws Exception {
        // given
        ConfirmedPayment expectedResponse = new ConfirmedPayment("paymentKey", "orderId", 10);
        mockRestServiceServer.expect(requestTo(CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(expectedResponse), MediaType.APPLICATION_JSON));

        NewPayment newPayment = new NewPayment("paymentKey", "orderId", 10L, "paymentType");

        // when
        ConfirmedPayment response = tossPaymentClient.confirm(newPayment);

        // then
        assertThat(response.orderId()).isEqualTo(expectedResponse.orderId());
    }

    @Test
    @DisplayName("토스 결제 승인 API에서 잘못된 요청 응답을 받는다.")
    void confirmWithBadRequest() {
        // given
        String expectedResponse = """
                {
                  "code": "REJECT_CARD_PAYMENT",
                  "message": "한도초과 혹은 잔액부족으로 결제에 실패했습니다."
                }
                """;
        mockRestServiceServer.expect(requestTo(CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(expectedResponse)
                        .contentType(MediaType.APPLICATION_JSON));

        NewPayment newPayment = new NewPayment("paymentKey", "orderId", 10L, "paymentType");

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirm(newPayment))
                .isInstanceOf(ViolationException.class);
    }

    @Test
    @DisplayName("토스 결제 승인 API에서 서버 오류 응답을 받는다.")
    void confirmWithServerError() {
        // given
        String expectedResponse = """
                {
                  "code": "FAILED_CARD_COMPANY_RESPONSE",
                  "message": "카드사에서 에러가 발생했습니다. 잠시 후 다시 시도해 주세요."
                }
                """;
        mockRestServiceServer.expect(requestTo(CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(expectedResponse)
                        .contentType(MediaType.APPLICATION_JSON));

        NewPayment newPayment = new NewPayment("paymentKey", "orderId", 10L, "paymentType");

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirm(newPayment))
                .isInstanceOf(PaymentServerException.class);
    }
}
