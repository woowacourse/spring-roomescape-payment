package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.config.TestTossPaymentConfig;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.exception.PaymentProcessException;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.interceptor.TossPaymentResponseInterceptor;

@RestClientTest(TossPaymentClient.class)
@Import({TestTossPaymentConfig.class, TossPaymentResponseInterceptor.class})
class TossPaymentClientTest {

    private static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments";

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTossPaymentConfig testTossPaymentConfig;

    private TossPaymentClient tossPaymentClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = testTossPaymentConfig.restClientBuilder();
        mockServer = MockRestServiceServer.bindTo(builder).build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(builder.build()))
                .build();

        tossPaymentClient = factory.createClient(TossPaymentClient.class);
    }

    @Test
    void 결제_승인을_할_수_있다() throws JsonProcessingException {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        PaymentResponse paymentResponse = new PaymentResponse(orderId);
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withSuccess(objectMapper.writeValueAsString(paymentResponse), MediaType.APPLICATION_JSON));

        PaymentResponse result = tossPaymentClient.getPaymentConfirm(request);

        Assertions.assertThat(result.orderId()).isEqualTo(orderId);
    }

    @Test
    void 보내야하는_값이_없다면_예외를_반환한다() throws JsonProcessingException {
        String paymentKey = "";
        String orderId = "";
        Long amount = 10000L;

        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        String expectedMessage = "결제 키와 주문 ID는 필수입니다.";

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"결제 키와 주문 ID는 필수입니다.\"}"));

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(PaymentProcessException.class)
                .hasMessage(expectedMessage);
    }

    //    @Disabled("타임 아웃 테스트")
    @Test
    void 결제_서비스에_타임아웃이_발생한_경우_예외_반환() throws JsonProcessingException {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(requests -> {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ignored) {
                            }

                            return new MockClientHttpResponse(
                                    "{\"message\":\"결제 서비스에 연결할 수 없습니다.\"}".getBytes(),
                                    HttpStatus.BAD_REQUEST
                            );
                        }
                );

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(PaymentProcessException.class)
                .hasMessage("결제 서비스에 연결할 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INVALID_API_KEY",
            "NOT_FOUND_TERMINAL_ID",
            "INVALID_AUTHORIZE_AUTH",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT",
    })
    void 서버_오류가_발생하면_예외를_반환한다(String code) throws JsonProcessingException {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"code\":\"" + code + "\"}"));

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(PaymentServerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PROVIDER_ERROR",
            "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
            "FAILED_INTERNAL_SYSTEM_PROCESSING"
    })
    void 일시적인_오류가_발생하면_예외를_반환한다(String code) throws JsonProcessingException {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"code\":\"" + code + "\"}"));

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(PaymentTemporaryException.class);
    }
}
