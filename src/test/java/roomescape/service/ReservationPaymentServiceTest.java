package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.exception.TossPaymentException;
import roomescape.service.ReservationPaymentServiceTest.TestRestClientConfig;

@SpringBootTest
@Import(TestRestClientConfig.class)
class ReservationPaymentServiceTest {

//    @Autowired
//    private RestClient.Builder clientBuilder;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private PaymentClientService paymentClientService;
//
//    private ReservationPaymentService reservationPaymentService;
//
//    private MockRestServiceServer backend;
//
//    @BeforeEach
//    void setUp() throws IOException {
//
//        backend = backend.bindTo(clientBuilder).build();
//
//        reservationPaymentService = new ReservationPaymentService();
//    }
//
//    @DisplayName("토스 결제 승인 요청이 실패했을 때 예외를 발생한다.")
//    @Test
//    void requestConfirmPaymentWithValidResponse() throws JsonProcessingException {
//        //given
//        String jsonResponse = objectMapper.writeValueAsString(new TestData("NOT_FOUND_PAYMENT", "존재하지 않는 결제 입니다."));
//        backend.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withBadRequest().body(jsonResponse));
//
//        // when
//        assertThatThrownBy(() -> paymentClientService.confirm("secretToken",
//                new PaymentConfirmRequest("orderId", 50000, "paymentKey", "TOSS")))
//                .isInstanceOf(TossPaymentException.class)
//                .hasMessage(jsonResponse);
//    }

    @TestConfiguration
    static class TestRestClientConfig {

        @Bean
        public RestClient restClient(MockWebServer mockWebServer) {
            return RestClient.builder()
                    .baseUrl(mockWebServer.url("/v1").toString()) // ⬅ mockWebServer의 주소로 설정!
                    .build();
        }

        @Bean
        public PaymentClientService paymentClientService(RestClient restClient) {
            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
            return factory.createClient(PaymentClientService.class);
        }
    }

    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PaymentClientService paymentClientService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DisplayName("토스 결제 승인 요청이 실패했을 때 예외를 발생한다.")
    @Test
    void confirmPaymentFail_shouldThrowTossPaymentException() throws Exception {
        // given
        String errorResponse = objectMapper.writeValueAsString(
                new TestData("NOT_FOUND_PAYMENT", "존재하지 않는 결제 입니다.")
        );

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", APPLICATION_JSON.toString())
                .setBody(errorResponse)
        );

        // when & then
        assertThatThrownBy(() -> paymentClientService.confirm(
                "secretToken",
                new PaymentConfirmRequest("orderId", 50000, "paymentKey", "TOSS")))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("존재하지 않는 결제 입니다.");
    }
}