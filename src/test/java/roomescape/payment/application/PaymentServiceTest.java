package roomescape.payment.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.common.ServiceTest;
import roomescape.global.exception.ViolationException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentProduct;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.pg.TossPaymentsClient;
import roomescape.payment.pg.TossPaymentsPayment;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class PaymentServiceTest extends ServiceTest {
    @MockBean
    private TossPaymentsClient client;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("결제를 승인한다.")
    void confirm() {
        TossPaymentsPayment request = Mockito.mock(TossPaymentsPayment.class);
        BDDMockito.when(request.verify(any()))
                .thenReturn(true);
        BDDMockito.when(client.findBy(any()))
                .thenReturn(request);
        PaymentConfirmRequest confirmRequest = createRequest("paymentKey");
        PaymentProduct product = new PaymentProduct(1L);

        paymentService.confirm(confirmRequest, product);

        Optional<Payment> payment = paymentRepository.findById("paymentKey");

        assertThat(payment).isNotEmpty();
    }

    @Test
    @DisplayName("승인 정보가 잘못된 경우, 결제를 승인할 수 없다.")
    void noConfirm() {
        TossPaymentsPayment request = Mockito.mock(TossPaymentsPayment.class);
        BDDMockito.when(request.verify(any()))
                .thenReturn(false);
        BDDMockito.when(client.findBy(any()))
                .thenReturn(request);
        PaymentConfirmRequest confirmRequest = createRequest("paymentKey");
        PaymentProduct product = new PaymentProduct(1L);

        assertThatThrownBy(() -> paymentService.confirm(confirmRequest, product))
                .isInstanceOf(ViolationException.class)
                .hasMessage("올바른 결제 정보를 입력해주세요.");
    }

    private PaymentConfirmRequest createRequest(String paymentKey) {
        return new PaymentConfirmRequest(
                paymentKey,
                "orderId",
                BigDecimal.valueOf(1000L),
                "card"
        );
    }
}
