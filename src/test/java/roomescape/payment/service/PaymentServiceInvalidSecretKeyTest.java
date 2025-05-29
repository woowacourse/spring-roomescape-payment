package roomescape.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import roomescape.payment.domain.dto.PaymentRequestDto;
import roomescape.payment.exception.InvalidPaymentException;

@TestPropertySource(properties = {
        "toss.payment.secret-key=tosstasstosstasstoss"
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PaymentServiceInvalidSecretKeyTest {

    @Autowired
    private PaymentService paymentService;

    @DisplayName("secretKey가 유효하지 않을 때 예외 발생 : InvalidPaymentException")
    @Test
    void approve_throwsException_byInvalidSecretKey() {
        // given
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        // when & then
        Assertions.assertThatThrownBy(
                        () -> paymentService.approve(dto)
                ).isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.");
    }
}
