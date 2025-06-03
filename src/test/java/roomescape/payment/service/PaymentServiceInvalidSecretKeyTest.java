package roomescape.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientException;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.toss.service.TossPaymentService;

@TestPropertySource(properties = {
        "toss.payment.secret-key=tosstasstosstasstoss"
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PaymentServiceInvalidSecretKeyTest {

    @Autowired
    private TossPaymentService paymentService;

    @DisplayName("secretKey가 유효하지 않을 때 예외 발생 : RestClientException")
    @Test
    void approve_throwsException_byInvalidSecretKey() {
        // given
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        // when & then
        Assertions.assertThatThrownBy(
                () -> paymentService.approve(dto)
        ).isInstanceOf(RestClientException.class);
    }
}
