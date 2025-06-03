package roomescape.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.exception.InvalidPaymentException;
import roomescape.payment.toss.TossPaymentRestClient;
import roomescape.payment.global.domain.Payment;
import roomescape.payment.toss.service.TossPaymentService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PaymentServiceTest {

    @Test
    void 결제_승인_요청시_200_OK() {
        TossPaymentRestClient mockRestClient = mock(TossPaymentRestClient.class);
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        Payment dummyPayment = new Payment("paymentKey", "orderId", 1000, "NORMAL");
        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenReturn(dummyPayment);

        TossPaymentService paymentService = new TossPaymentService(mockRestClient);

        Assertions.assertThatCode(
                () -> paymentService.approve(dto)
        ).doesNotThrowAnyException();
    }

    @Test
    void 결제_승인_요청시_400에러가_발생하면_InvalidPaymentException_발생() {
        TossPaymentRestClient mockRestClient = mock(TossPaymentRestClient.class);
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenThrow(new InvalidPaymentException(HttpStatus.BAD_REQUEST));

        TossPaymentService paymentService = new TossPaymentService(mockRestClient);

        Assertions.assertThatThrownBy(
                () -> paymentService.approve(dto)
        ).isInstanceOf(InvalidPaymentException.class);
    }
}
