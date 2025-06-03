package roomescape.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.payment.global.domain.Payment;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.exception.InvalidPaymentException;
import roomescape.payment.toss.TossPaymentRestClient;
import roomescape.payment.toss.service.TossPaymentService;

public class PaymentServiceTest {

    private final TossPaymentRestClient mockRestClient = mock(TossPaymentRestClient.class);

    private PaymentRequestDto paymentRequestDto;

    @BeforeEach
    void setUp() {
        paymentRequestDto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");
    }

    @Test

    void 결제_승인_요청시_200_OK() {
        Payment dummyPayment = new Payment("paymentKey", "orderId", 1000, "NORMAL");
        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenReturn(dummyPayment);

        TossPaymentService paymentService = new TossPaymentService(mockRestClient);

        Assertions.assertThatCode(
                () -> paymentService.approve(paymentRequestDto)
        ).doesNotThrowAnyException();
    }

    @Test
    void 결제_승인_요청시_400에러가_발생하면_InvalidPaymentException_발생() {
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenThrow(new InvalidPaymentException(HttpStatus.BAD_REQUEST));

        TossPaymentService paymentService = new TossPaymentService(mockRestClient);

        Assertions.assertThatThrownBy(
                () -> paymentService.approve(dto)
        ).isInstanceOf(InvalidPaymentException.class);
    }
}
