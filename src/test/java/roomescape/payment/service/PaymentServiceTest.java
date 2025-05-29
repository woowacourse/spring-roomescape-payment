package roomescape.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;
import roomescape.payment.TossRestClient;
import roomescape.payment.domain.TossPayment;
import roomescape.payment.domain.dto.PaymentRequestDto;
import roomescape.payment.exception.InvalidPaymentException;

@TestPropertySource(properties = {
        "toss.payment.secret-key=tosstasstosstasstoss",
        "toss.payment.base-url=https://baseurlisinvalid"
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void 결제_승인_요청시_200_OK() {
        TossRestClient mockRestClient = mock(TossRestClient.class);
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        TossPayment dummyPayment = new TossPayment("paymentKey", "orderId", 1000, "NORMAL");
        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenReturn(dummyPayment);

        PaymentService paymentService = new PaymentService(mockRestClient);

        Assertions.assertThatCode(
                () -> paymentService.approve(dto)
        ).doesNotThrowAnyException();
    }

    @Test
    void 결제_승인_요청시_400에러가_발생하면_InvalidPaymentException_발생() {
        TossRestClient mockRestClient = mock(TossRestClient.class);
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenThrow(new InvalidPaymentException(HttpStatus.BAD_REQUEST));

        PaymentService paymentService = new PaymentService(mockRestClient);

        Assertions.assertThatThrownBy(
                () -> paymentService.approve(dto)
        ).isInstanceOf(InvalidPaymentException.class);
    }

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

    @DisplayName("잘못된 baseUrl로 요청했을 때 예외 발생")
    @Test
    void approve_throwsException_byInvalidBaseUrl() {
        // given
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        // when & then

        Assertions.assertThatThrownBy(
                () -> paymentService.approve(dto)
        ).isInstanceOf(ResourceAccessException.class);
    }
}
