package roomescape.payment.service;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.config.TestPaymentConfig;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.interceptor.PaymentResponseInterceptor;

@RestClientTest(PaymentClient.class)
@Import({TestPaymentConfig.class, PaymentResponseInterceptor.class})
class PaymentClientTest {

    private static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments";

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestClient.Builder builder;

    @Autowired
    private TestPaymentConfig testPaymentConfig;

    private PaymentClient paymentClient;

    // https://api.tosspayments.com/v1/payments
    //  parameterkey, orderId, amount

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = testPaymentConfig.restClientBuilder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        testPaymentConfig.paymentClient(builder);

        RestClient build = builder
                .baseUrl(PAYMENT_URL)
                .requestInterceptor(new PaymentResponseInterceptor(objectMapper))
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(build))
                .build();

        paymentClient = factory.createClient(PaymentClient.class);
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

        PaymentResponse result = paymentClient.getPaymentConfirm(request);

        mockServer.verify();

        System.out.println(result);
        Assertions.assertThat(result.orderId()).isEqualTo(orderId);
    }

    @Test
    void 보내야하는_값이_없다면_예외를_반환한다() {

    }

    @Test
    void 결제_서비스에_연결이_되지_않을_때() {

    }

    //  결제 서비스에서 예외가 발생할 때

}
