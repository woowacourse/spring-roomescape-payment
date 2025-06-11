package roomescape.payment.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.payment.application.dto.PaymentConfirmRequest;
import roomescape.payment.application.dto.PrePaymentValidRequest;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void 결제_승인을_대기한다() {
        // given
        final PaymentConfirmRequest request = new PaymentConfirmRequest(
                "dummyPaymentKey",
                "dummyOrderId",
                BigDecimal.valueOf(1000)
        );

        final Reservation reservation = new Reservation(1L, null, null, null, null);

        // when
        final Payment payment = paymentService.pend(request, reservation);

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void 결제_승인을_성공한다() {
        // given
        final Payment payment = Payment.pending(
                "dummyOrderId",
                "dummyPaymentKey",
                BigDecimal.valueOf(1000),
                new Reservation(1L, null, null, null, null)
        );
        paymentRepository.save(payment);

        // when
        paymentService.success(payment);

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void 결제_승인을_실패한다() {
        // given
        final Payment payment = Payment.pending(
                "dummyOrderId",
                "dummyPaymentKey",
                BigDecimal.valueOf(1000),
                new Reservation(1L, null, null, null, null)
        );
        paymentRepository.save(payment);

        // when
        paymentService.fail(payment);

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void 결제_요청을_대기한다() {
        // given
        final PrePaymentValidRequest request = new PrePaymentValidRequest(
                "dummyOrderId",
                "dummyPaymentKey",
                BigDecimal.valueOf(1000)
        );

        final Reservation reservation = new Reservation(1L, null, null, null, null);

        // when
        final Payment payment = paymentService.await(request, reservation);

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.AWAIT);
    }
}
