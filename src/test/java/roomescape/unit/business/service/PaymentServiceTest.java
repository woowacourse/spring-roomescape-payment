package roomescape.unit.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.vo.PaymentStatus;
import roomescape.business.service.PaymentService;
import roomescape.exception.payment.PaymentNotFoundException;
import roomescape.infrastructure.PaymentRepository;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.presentation.dto.request.PaymentRequest;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private final PaymentService paymentService;
    PaymentRepository paymentRepository = mock(PaymentRepository.class);
    PaymentClient paymentClient = mock(PaymentClient.class);

    public PaymentServiceTest() {
        this.paymentService = new PaymentService(paymentRepository, paymentClient);
    }

    @Test
    void 결제를_생성한다() {
        // given
        PaymentRequest request = new PaymentRequest("orderId1", 1000L);
        given(paymentRepository.existsByOrderId("orderId1")).willReturn(false);
        Payment payment = Payment.create("orderId", 1000L);
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);
        // when
        String paymentId = paymentService.createPayment(request);
        // then
        assertThat(paymentId).isEqualTo(payment.getId().id());
    }

    @Test
    void 결제를_승인한다() {
        // given
        Reservation reservation = Reservation.restore("id1", null, null, null, null);
        Payment payment = Payment.restore("id", "orderId", null, 1000L, PaymentStatus.IN_PROGRESS, null);
        given(paymentRepository.findByOrderId("orderId")).willReturn(Optional.of(payment));
        // when
        assertThatCode(() -> paymentService.approvePayment(reservation, "paymentKey", "orderId", 1000L))
                .doesNotThrowAnyException();
    }

    @Test
    void 결제가_존재하지_않으면_예외가_발생한다() {
        // given
        Reservation reservation = Reservation.restore("id1", null, null, null, null);
        given(paymentRepository.findByOrderId("orderId")).willReturn(Optional.empty());
        // when
        assertThatThrownBy(() -> paymentService.approvePayment(reservation, "paymentKey", "orderId", 1000L))
                .isInstanceOf(PaymentNotFoundException.class);
    }
}