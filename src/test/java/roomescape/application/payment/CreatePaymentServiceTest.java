package roomescape.application.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.application.AbstractServiceIntegrationTest;
import roomescape.application.payment.dto.CreatePaymentCommand;
import roomescape.domain.payment.repository.PaymentRepository;

class CreatePaymentServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private CreatePaymentService createPaymentService;

    @BeforeEach
    void setUp() {
        createPaymentService = new CreatePaymentService(paymentRepository);
    }

    @Test
    void 결제_생성_서비스_테스트() {
        // given
        CreatePaymentCommand command = new CreatePaymentCommand("orderId", 10000L);

        // when
        Long paymentId = createPaymentService.register(command);

        // then
        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(payment -> {
                    assertThat(payment.getOrderId()).isEqualTo("orderId");
                    assertThat(payment.getAmount()).isEqualTo(10000L);
                });
    }
}
