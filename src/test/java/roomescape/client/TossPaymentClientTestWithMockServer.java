package roomescape.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.client.dto.PaymentConfirmResultDto;
import roomescape.config.HttpClientConfiguration;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;
import roomescape.service.dto.PaymentConfirmDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
@Import(HttpClientConfiguration.class)
class TossPaymentClientTestWithMockServer {

    public static final String PAYMENTS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    @Autowired
    private RestClient.Builder builder;

    @Autowired
    private ObjectMapper objectMapper;

    private TossPaymentClient tossPaymentClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient client = builder.build();
        tossPaymentClient = new TossPaymentClient(client);
    }

    @Test
    @DisplayName("UNAUTHORIZED_KEY 예외를 받으면 PaymentConfirmServerException를 던진다")
    void test1() throws JsonProcessingException {
        // given
        PaymentConfirmDto requestDto = new PaymentConfirmDto("paymentKey", "orderId", 10000L);
        String errorResponseBody = """
                    {
                      "code": "UNAUTHORIZED_KEY",
                      "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                    }
                """;

        mockServer.expect(requestTo(PAYMENTS_CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(
                        withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponseBody)
                );

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirmPayment(requestDto))
                .isInstanceOf(PaymentConfirmServerException.class);
    }

    @Test
    @DisplayName("사용자에게 보여줘도 되는 메시지의 예외는 그대로 PaymentConfirmClientException를 던진다.")
    void test2() throws JsonProcessingException {
        // given
        PaymentConfirmDto requestDto = new PaymentConfirmDto("paymentKey", "orderId", 10000L);

        String errorResponseBody = """
                    {
                      "code": "ALREADY_PROCESSED_PAYMENT",
                      "message": "이미 처리된 결제 입니다."
                    }
                """;

        mockServer.expect(requestTo(PAYMENTS_CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(errorResponseBody)
                );

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirmPayment(requestDto))
                .isInstanceOf(PaymentConfirmClientException.class);
    }

    @Test
    @DisplayName("정상 흐름 진행 테스트")
    void test3() throws JsonProcessingException {
        // given
        PaymentConfirmDto requestDto = new PaymentConfirmDto("paymentKey", "orderId", 10000L);

        Payment payment = new Payment(requestDto.paymentKey(), requestDto.orderId(), requestDto.amount(), new Reservation());

        mockServer.expect(requestTo(PAYMENTS_CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(payment), MediaType.APPLICATION_JSON));

        // when
        PaymentConfirmResultDto result = tossPaymentClient.confirmPayment(requestDto);

        //then
        assertThat(result.paymentKey()).isEqualTo(payment.getPaymentKey());
        assertThat(result.orderId()).isEqualTo(payment.getOrderId());
        assertThat(result.totalAmount()).isEqualTo(payment.getTotalAmount());
    }
}