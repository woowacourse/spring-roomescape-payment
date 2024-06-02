package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;

import roomescape.exception.PaymentException;
import roomescape.reservation.dto.PaymentRequest;
import roomescape.reservation.dto.PaymentResponse;
import roomescape.reservation.encoder.TossSecretKeyEncoder;

@RestClientTest(PaymentService.class)
class PaymentServiceTest {

    private static final String status = "status";
    private static final String paymentKey = "paymentKey";
    private static final String orderId = "orderId";

    @Value("${custom.security.toss-payment.secret-key}")
    private String secretKey;

    private String encodedSecretKey;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        encodedSecretKey = TossSecretKeyEncoder.encode(secretKey);
    }

    @DisplayName("토스 결제를 정상적으로 처리한다.")
    @Test
    void requestTossPaymentTest() {
        // Given
        String json = getExpectedPaymentResponse(status, paymentKey, orderId);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", encodedSecretKey))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        // When
        PaymentResponse paymentResponse = paymentService.requestTossPayment(
                new PaymentRequest(paymentKey, orderId, 1000L)
        );

        // Then
        server.verify();
        assertThat(paymentResponse.paymentKey()).isEqualTo(paymentKey);
        assertThat(paymentResponse.orderId()).isEqualTo(orderId);
        assertThat(paymentResponse.status()).isEqualTo(status);
    }

    @DisplayName("시크릿 키를 정상적이지 않은 값으로 요청하면 예외가 발생한다.")
    @Test
    void throwExceptionWhenInvalidSecretKey() {
        // Given
        String wrongEncodedSecretKey = encodedSecretKey;

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", wrongEncodedSecretKey))
                .andRespond(withBadRequest().body("{message:\"잘못된 시크릿키 연동 정보입니다.\"}".getBytes()));

        // When & Then
        assertThatThrownBy(() -> paymentService.requestTossPayment(new PaymentRequest(paymentKey, orderId, 1000L)))
                .isInstanceOfSatisfying(PaymentException.class, e -> {
                    assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(e.getMessage()).contains("잘못된 시크릿키 연동 정보입니다.");
                });
    }

    @DisplayName("paymentKey를 클라이언트에서 획득하지 않은 값으로 요청 시 예외가 발생한다.")
    @Test
    void throwExceptionWhenInvalidPaymentKey() {
        // Given
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", encodedSecretKey))
                .andRespond(withBadRequest().body("{message:\"잘못된 요청입니다.\"}".getBytes()));

        // When & Then
        assertThatThrownBy(() -> paymentService.requestTossPayment(new PaymentRequest(paymentKey, orderId, 1000L)))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("잘못된 요청입니다.");
    }

    @DisplayName("이미 성공한 paymentKey로 요청하면 예외가 발생한다.")
    @Test
    void throwExceptionWhenUsedPaymentKey() {
        // Given
        String usedPaymentKey = "usedPaymentKey";

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", encodedSecretKey))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockRestRequestMatchers.jsonPath("$.paymentKey", Matchers.equalToIgnoringCase(usedPaymentKey)))
                .andRespond(withBadRequest().body("{message:\"이미 처리된 결제 입니다.\"}".getBytes()));

        // When & Then
        assertThatThrownBy(() -> paymentService.requestTossPayment(new PaymentRequest(usedPaymentKey, orderId, 1000L)))
                .isInstanceOfSatisfying(PaymentException.class, e -> {
                    assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(e.getMessage()).contains("이미 처리된 결제 입니다.");
                });
    }

    private String getExpectedPaymentResponse(String status, String paymentKey, String orderId) {
        try {
            return new JSONObject()
                    .put("status", status)
                    .put("paymentKey", paymentKey)
                    .put("orderId", orderId)
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException();
        }
    }
}
