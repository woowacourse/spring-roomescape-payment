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
import java.util.List;
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

    @Test
    @DisplayName("예약 ID들에 해당되는 모든 결제들을 조회한다.")
    void findAllInPaymentProduct() {
        PaymentProduct product1 = new PaymentProduct(1L);
        PaymentProduct product2 = new PaymentProduct(2L);
        PaymentProduct product3 = new PaymentProduct(3L);
        Payment payment1 = createPayment("paymentKey1", "orderId1", product1);
        Payment payment2 = createPayment("paymentKey2", "orderId2", product2);
        Payment payment3 = createPayment("paymentKey3", "orderId3", product3);
        paymentRepository.saveAndFlush(payment1);
        paymentRepository.saveAndFlush(payment2);
        paymentRepository.saveAndFlush(payment3);

        List<PaymentProduct> products = List.of(product1, product2, product3);
        List<Payment> payments = paymentService.findAllInPaymentProducts(products);

        assertThat(payments).hasSize(3);
    }

    private Payment createPayment(String paymentKey, String orderId, PaymentProduct paymentProduct) {
        return new Payment(paymentKey, orderId, BigDecimal.valueOf(1000L), paymentProduct);
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
