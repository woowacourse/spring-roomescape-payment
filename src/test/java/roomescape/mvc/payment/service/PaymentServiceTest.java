package roomescape.mvc.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.exception.PaymentException;
import roomescape.mvc.payment.dto.PaymentCreationContent;
import roomescape.mvc.payment.dto.PaymentResult;
import roomescape.mvc.payment.repository.PaymentRepository;
import roomescape.test.stub.PaymentClientStub;

@DataJpaTest
@Import(value = {PaymentService.class, PaymentClientStub.class})
class PaymentServiceTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentClientStub paymentClientStub;

    @Nested
    @DisplayName("결제 데이터를 저장할 수 있다.")
    public class savePayment {

        @DisplayName("정상적으로 결제 데이터를 저장할 수 있다.")
        @Test
        void canSavePayment() {
            // given
            PaymentResult paymentResult = new PaymentResult("order_id", "payment_key", 1000L);
            paymentClientStub.setAuthorizePayment(paymentResult);

            PaymentCreationContent creationContent = new PaymentCreationContent(
                    paymentResult.orderId(), paymentResult.paymentKey(), paymentResult.totalAmount());

            // when
            paymentService.savePayment(creationContent);

            // then
            assertThat(paymentRepository.findAll()).hasSize(1);
        }

        @DisplayName("결제 승인에 실패할 경우 결제 데이터를 생성할 수 없다.")
        @Test
        void cannotSavePayment() {
            // given
            PaymentException exception = new PaymentException("결제 승인 실패");
            paymentClientStub.setAuthorizePayment(exception);

            PaymentCreationContent creationContent = new PaymentCreationContent(
                    "order_id", "payment_key", 1000L);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> paymentService.savePayment(creationContent))
                            .isInstanceOf(PaymentException.class),
                    () -> assertThat(paymentRepository.findAll()).hasSize(0)
            );
        }
    }
}
