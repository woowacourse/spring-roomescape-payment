package roomescape.controller.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClient;
import roomescape.config.RestClientConfiguration;
import roomescape.exception.PaymentConfirmServerException;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RestClientConfiguration.class)
public class PaymentConfirmTest {

    @Autowired
    private RestClient restClient;

    @DisplayName("결제 예외 테스트 - INVALID_API_KEY")
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
}
