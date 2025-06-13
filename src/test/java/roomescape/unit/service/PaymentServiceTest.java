package roomescape.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;
import roomescape.exception.custom.PaymentException;
import roomescape.repository.PaymentRepository;
import roomescape.service.PaymentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Reservation reservation;

    static final String BASE_URL = "https://api.tosspayments.com";
    static final String API_URL = BASE_URL + "/v1/payments/confirm";

    final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl(BASE_URL);

    MockRestServiceServer mockServer = MockRestServiceServer.bindTo(testBuilder).build();

    PaymentService paymentService;

    @BeforeEach
    void setUp() {
        mockServer.reset();
        paymentService = new PaymentService(paymentRepository, testBuilder.build(), new ObjectMapper());
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
                    "totalAmount": "1000",
                    "type": "paymentType"
                }
                """;

        mockServer
                .expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // when
        ConfirmPaymentResponse actual = paymentService.confirmPayment(request, reservation);

        // then
        ConfirmPaymentResponse expected = new ConfirmPaymentResponse("testPaymentKey", "testOrderId", 1000, "paymentType");
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
        Assertions.assertThatThrownBy(() -> paymentService.confirmPayment(request, reservation))
                .isInstanceOf(PaymentException.class);
        mockServer.verify();
    }
}
