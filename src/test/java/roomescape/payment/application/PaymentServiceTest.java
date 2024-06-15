package roomescape.payment.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.common.ServiceTest;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentProduct;
import roomescape.payment.domain.PaymentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static roomescape.TestFixture.PRODUCT_PAY_REQUEST;
import static roomescape.TestFixture.SIMPLE_PAYMENT;

class PaymentServiceTest extends ServiceTest {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private PaymentGateway paymentGateway;

    @Test
    @DisplayName("신규 결제를 생성한다.")
    void pay() {
        PaymentProduct product = new PaymentProduct(1L);
        Payment givenPayment = SIMPLE_PAYMENT("paymentKey", "orderId", product);
        BDDMockito.when(paymentGateway.createPayment(any(), any()))
                .thenReturn(givenPayment);

        ProductPayRequest request = PRODUCT_PAY_REQUEST("paymentKey", "orderId");
        paymentService.pay(request, product);

        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);
    }

    @Test
    @DisplayName("예약 ID들에 해당되는 모든 결제들을 조회한다.")
    void findAllInPaymentProduct() {
        PaymentProduct product1 = new PaymentProduct(1L);
        PaymentProduct product2 = new PaymentProduct(2L);
        PaymentProduct product3 = new PaymentProduct(3L);
        Payment payment1 = SIMPLE_PAYMENT("paymentKey1", "orderId1", product1);
        Payment payment2 = SIMPLE_PAYMENT("paymentKey2", "orderId2", product2);
        Payment payment3 = SIMPLE_PAYMENT("paymentKey3", "orderId3", product3);
        paymentRepository.saveAndFlush(payment1);
        paymentRepository.saveAndFlush(payment2);
        paymentRepository.saveAndFlush(payment3);

        List<PaymentProduct> products = List.of(product1, product2, product3);
        List<Payment> payments = paymentService.findAllInPaymentProducts(products);

        assertThat(payments).hasSize(3);
    }
}
