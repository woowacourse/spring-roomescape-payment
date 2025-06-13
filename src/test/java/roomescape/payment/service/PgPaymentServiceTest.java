package roomescape.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.payment.global.domain.PgPayment;
import roomescape.payment.global.domain.dto.PgPaymentRequestDto;
import roomescape.payment.global.domain.dto.PgPaymentDataDto;
import roomescape.payment.global.exception.InvalidPgPaymentException;
import roomescape.payment.global.service.PgPaymentService;
import roomescape.payment.tosspayment.TossPgPaymentRestClient;

public class PgPaymentServiceTest {

    private final TossPgPaymentRestClient mockRestClient = mock(TossPgPaymentRestClient.class);

    private PgPaymentRequestDto pgPaymentRequestDto;

    @BeforeEach
    void setUp() {
        pgPaymentRequestDto = new PgPaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");
    }

    @Test
    void 결제_승인_요청시_200_OK() {
        PgPayment pgPayment = new PgPayment("paymentKey", "orderId", 1000, "NORMAL");
        PgPaymentDataDto pgPaymentDataDto = new PgPaymentDataDto("paymentKey", "orderId", 1000, "NORMAL");
        when(mockRestClient.confirmPayment(any(PgPaymentRequestDto.class)))
                .thenReturn(pgPaymentDataDto);

        PgPaymentService pgPaymentService = new PgPaymentService(mockRestClient);

        Assertions.assertThatCode(
                () -> pgPaymentService.approve(pgPaymentRequestDto)
        ).doesNotThrowAnyException();
    }

    @Test
    void 결제_승인_요청시_400에러가_발생하면_InvalidPaymentException_발생() {
        PgPaymentRequestDto dto = new PgPaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        when(mockRestClient.confirmPayment(any(PgPaymentRequestDto.class)))
                .thenThrow(new InvalidPgPaymentException(HttpStatus.BAD_REQUEST));

        PgPaymentService pgPaymentService = new PgPaymentService(mockRestClient);

        Assertions.assertThatThrownBy(
                () -> pgPaymentService.approve(dto)
        ).isInstanceOf(InvalidPgPaymentException.class);
    }

    @Test
    void 결제_승인_요청시_500에러가_발생하면_InvalidPaymentException_발생() {
        PgPaymentRequestDto dto = new PgPaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        when(mockRestClient.confirmPayment(any(PgPaymentRequestDto.class)))
                .thenThrow(new InvalidPgPaymentException(HttpStatus.INTERNAL_SERVER_ERROR));

        PgPaymentService pgPaymentService = new PgPaymentService(mockRestClient);

        Assertions.assertThatThrownBy(
                () -> pgPaymentService.approve(dto)
        ).isInstanceOf(InvalidPgPaymentException.class);
    }
}
