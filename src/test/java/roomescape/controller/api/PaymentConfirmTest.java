package roomescape.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClient;
import roomescape.client.PaymentClient;
import roomescape.config.RestClientConfiguration;
import roomescape.dto.reservation.TossPaymentConfirmRequestDto;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RestClientConfiguration.class, PaymentClient.class, ObjectMapper.class})
public class PaymentConfirmTest {

    @Autowired
    private RestClient restClient;

    @Autowired
    private PaymentClient paymentClient;

    @DisplayName("결제 예외 핸들링 테스트 - INVALID_API_KEY가 발생할 경우")
    @Test
    void paymentExceptionTest() {
        assertThatThrownBy(
                () -> restClient.post()
                        .uri("https://api.tosspayments.com/v1/payments/confirm")
                        .header("Authorization", "Basic " +
                                Base64.getEncoder().encodeToString("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:".getBytes()))
                        .header("TossPayments-Test-Code", "INVALID_API_KEY")
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(PaymentConfirmServerException.class);
    }

    @DisplayName("paymentkey를 클라이언트에서 획득하지 않은 값으로 요청 테스트")
    @Test
    void invalidPaymentKeyExceptionTest() {
        assertThatThrownBy(
                () -> paymentClient.confirmPayment(new TossPaymentConfirmRequestDto(
                        "wrongPaymentKey", "orderId", 1000L
                ))
        ).isInstanceOf(PaymentConfirmClientException.class);
    }
}
