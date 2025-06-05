package roomescape.payment.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.exception.impl.BadRequestException;
import roomescape.payment.application.PaymentClient;
import roomescape.payment.application.PaymentException;
import roomescape.payment.application.dto.PaymentConfirmRequest;
import roomescape.payment.application.dto.PaymentDataRequest;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.infrastructure.dto.TossPaymentRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.Theme;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TossPaymentServiceTest {

    @Autowired
    private TossPaymentService paymentService;

    @MockitoBean
    private PaymentClient paymentClient;

    @Test
    void 결제한다() {
        // given
        final PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                "dummy",
                "dummy",
                BigDecimal.valueOf(1000)
        );
        final PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                "dummy",
                "dummy",
                BigDecimal.valueOf(1000)
        );
        final Reservation reservation = new Reservation(1L, null, null, null, null);

        // when
        final Payment payment = paymentService.pay(paymentDataRequest, paymentConfirmRequest, reservation);

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void 결제_승인과정에서_예외가_발생하면_결제가_실패한다() {
        // given
        final String orderId = "dummy";
        final PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                orderId,
                "dummy",
                BigDecimal.valueOf(1000)
        );
        final PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                orderId,
                "dummy",
                BigDecimal.valueOf(1000)
        );
        final Reservation reservation = new Reservation(1L, null, null, null, null);

        when(paymentClient.requestPayment(any())).thenThrow(
                new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 승인 에러"));

        // when & then
        assertThatThrownBy(() -> paymentService.pay(paymentDataRequest, paymentConfirmRequest, reservation))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 승인 에러");
    }

    @Test
    void 결제_요청과_승인_사이의_orderId를_확인한다() {
        // given
        final PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                "dummy",
                "dummy",
                BigDecimal.valueOf(1000)
        );
        final PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                "dummy",
                "compromised",
                BigDecimal.valueOf(1000)
        );
        final Reservation reservation = new Reservation(1L, null, null, null, null);

        // when & then
        assertThatThrownBy(() -> paymentService.pay(paymentDataRequest, paymentConfirmRequest, reservation))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("결제 주문번호가 일치하지 않습니다.");
    }

    @Test
    void 결제_요청과_승인_사이의_amount를_확인한다() {
        // given
        final PaymentDataRequest paymentDataRequest = new PaymentDataRequest(
                "dummy",
                "dummy",
                BigDecimal.valueOf(100000)
        );
        final PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                "dummy",
                "dummy",
                BigDecimal.valueOf(1000)
        );
        final Reservation reservation = new Reservation(1L, null, null, null, null);

        // when & then
        assertThatThrownBy(() -> paymentService.pay(paymentDataRequest, paymentConfirmRequest, reservation))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("결제 금액이 일치하지 않습니다.");
    }

    @Test
    void 결제_요청을_대기한다() {
        // given
        final Theme theme = new Theme(1L, "dummy", "dummy", "dummy", BigDecimal.valueOf(10000));
        final Reservation reservation = new Reservation(1L, null, null, theme, null);

        // when
        final Payment payment = paymentService.await(reservation);

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.AWAIT);
    }

    @Test
    void 결제_요청이_실패하면_3번_재시도한다() {
        // given
        final PaymentRequest paymentRequest = new TossPaymentRequest(
                BigDecimal.valueOf(1000),
                "retryId",
                "retryKey"
        );
        when(paymentClient.requestPayment(any())).thenThrow(
                new TossPaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "재시도 테스트 실패"));

        // when & then
        assertThatThrownBy(() -> paymentService.paymentClientWithRetry(paymentRequest))
                .isInstanceOf(TossPaymentException.class)
                .hasMessage("재시도 테스트 실패");

        // 재시도 횟수 검증
        verify(paymentClient, times(3)).requestPayment(any());
    }
}
