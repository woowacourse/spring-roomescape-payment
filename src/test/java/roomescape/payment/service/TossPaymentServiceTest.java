package roomescape.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.payment.domain.client.TossRestClient;
import roomescape.payment.domain.dto.PaymentRequestDto;
import roomescape.payment.domain.dto.PaymentResponseDto;
import roomescape.payment.exception.InvalidPaymentException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TossPaymentServiceTest {

    private final TossRestClient mockRestClient = mock(TossRestClient.class);

    @Test
    void 결제_승인_요청시_200_OK() {
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        PaymentResponseDto dummyPaymentDto = new PaymentResponseDto("paymentKey", "orderId", 1000);
        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenReturn(dummyPaymentDto);

        TossPaymentService tossPaymentService = new TossPaymentService(mockRestClient);

        Assertions.assertThatCode(
                () -> tossPaymentService.approve(dto)
        ).doesNotThrowAnyException();
    }

    @Test
    void 결제_승인_요청시_400에러가_발생하면_InvalidPaymentException_발생() {
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenThrow(new InvalidPaymentException(HttpStatus.BAD_REQUEST));

        TossPaymentService tossPaymentService = new TossPaymentService(mockRestClient);

        Assertions.assertThatThrownBy(
                () -> tossPaymentService.approve(dto)
        ).isInstanceOf(InvalidPaymentException.class);
    }
}
