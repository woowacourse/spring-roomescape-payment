package roomescape.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import roomescape.domain.repository.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.exception.customexception.api.ApiBadRequestException;
import roomescape.exception.customexception.api.ApiException;
import roomescape.service.dto.request.PaymentApproveRequest;
import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.response.PaymentApproveResponse;
import roomescape.service.dto.response.PaymentCancelResponse;
import roomescape.service.reservation.pay.PaymentProperties;
import roomescape.service.reservation.pay.PaymentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@EnableConfigurationProperties({PaymentProperties.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
class PaymentServiceTest {

    @Autowired
    private PaymentProperties paymentProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private PaymentRepository paymentRepository;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @AfterEach
    void clear() {
        mockServer.reset();
    }


    @Test
    @DisplayName("성공 : 결제를 요청한다")
    void sucessPayment() {
        PaymentApproveRequest request = new PaymentApproveRequest("testKey", "testId", "1000");
        PaymentApproveResponse expectedResponse = new PaymentApproveResponse(request.paymentKey(), request.orderId());
        Reservation dummy = new Reservation(null, null);

        mockServer.expect(requestTo(paymentProperties.getApproveUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(makeJsonFrom(expectedResponse), MediaType.APPLICATION_JSON));

        PaymentApproveResponse actualResponse = paymentService.pay(request, dummy);

        assertAll(
                () -> mockServer.verify(),
                () -> assertThat(actualResponse.paymentKey()).isEqualTo(expectedResponse.paymentKey()),
                () -> assertThat(actualResponse.orderId()).isEqualTo(expectedResponse.orderId())
        );
    }

    @Test
    @DisplayName("성공 : 결제 취소를 요청한다")
    void sucessCancelPayment() {
        PaymentCancelRequest request = new PaymentCancelRequest("testKey", "결제 취소");
        PaymentCancelResponse expectedResponse = new PaymentCancelResponse(request.paymentKey(), "testId", "testOrder");

        mockServer.expect(requestTo(String.format(paymentProperties.getCancelUrl(), request.paymentKey())))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(makeJsonFrom(expectedResponse), MediaType.APPLICATION_JSON));

        PaymentCancelResponse actualResponse = paymentService.cancel(request);

        assertAll(
                () -> mockServer.verify(),
                () -> assertThat(actualResponse.paymentKey()).isEqualTo(expectedResponse.paymentKey()),
                () -> assertThat(actualResponse.orderId()).isEqualTo(expectedResponse.orderId()),
                () -> assertThat(actualResponse.orderName()).isEqualTo(expectedResponse.orderName())
        );
    }

    @Test
    @DisplayName("실패 : 400에러 발생 시 custom exception으로 예외가 전환된다.")
    void is4XXException_PaymentException() {
        PaymentApproveRequest request = new PaymentApproveRequest("testKey", "testId", "1000");
        TestErrorResponse response = new TestErrorResponse("INVALID_REQUEST", "test_error");
        Reservation dummy = new Reservation(null, null);

        mockServer.expect(ExpectedCount.manyTimes(), requestTo(paymentProperties.getApproveUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest().body(makeJsonFrom(response)));

        assertAll(
                () -> assertThatThrownBy(() -> paymentService.pay(request, dummy))
                        .isInstanceOf(ApiBadRequestException.class)
                        .hasMessage(response.message),
                () -> mockServer.verify()
        );
    }


    @Test
    @DisplayName("실패 : 500에러 반환시 custom exeption으로 예외가 전환된다.")
    void is5XXException_PaymentException() {
        PaymentApproveRequest request = new PaymentApproveRequest("testKey", "testId", "1000");
        TestErrorResponse response = new TestErrorResponse("INTERNAL_SERVER_ERROR", "test_error");
        Reservation dummy = new Reservation(null, null);

        mockServer.expect(ExpectedCount.manyTimes(), requestTo(paymentProperties.getApproveUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError().body(makeJsonFrom(response)));


        assertAll(
                () -> assertThatThrownBy(() -> paymentService.pay(request, dummy))
                        .isInstanceOf(ApiException.class),
                () -> mockServer.verify()
        );

    }

    @Test
    @DisplayName("에러 반환 시 총 5회 재시도한다.")
    void retryPayment() {
        PaymentApproveRequest request = new PaymentApproveRequest("testKey", "testId", "1000");
        TestErrorResponse response = new TestErrorResponse("INTERNAL_SERVER_ERROR", "test_error");
        Reservation dummy = new Reservation(null, null);

        mockServer.expect(ExpectedCount.times(2), requestTo(paymentProperties.getApproveUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError().body(makeJsonFrom(response)));

        assertAll(
                () -> assertThatThrownBy(() -> paymentService.pay(request, dummy))
                        .isInstanceOf(ApiException.class),
                () -> mockServer.verify()
        );
    }

    private String makeJsonFrom(PaymentApproveResponse response) {
        try {
            return new JSONObject()
                    .put("paymentKey", response.paymentKey())
                    .put("orderId", response.orderId())
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String makeJsonFrom(PaymentCancelResponse response) {
        try {
            return new JSONObject()
                    .put("paymentKey", response.paymentKey())
                    .put("orderId", response.orderId())
                    .put("orderName", response.orderName())
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String makeJsonFrom(TestErrorResponse response) {
        try {
            return new JSONObject()
                    .put("code", response.code)
                    .put("message", response.message)
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    static class TestErrorResponse {
        private final String code;
        private final String message;

        public TestErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
