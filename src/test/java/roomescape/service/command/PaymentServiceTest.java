package roomescape.service.command;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.exception.PaymentException;
import roomescape.repository.PaymentRepository;
import roomescape.test.stub.PaymentClientStub;

@DataJpaTest
class PaymentServiceTest {

    private static final String ALREADY_PROCESSED_PAYMENT = "이미 처리된 결제 입니다.";
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    private final PaymentClientStub paymentClientStub = new PaymentClientStub();

    @Test
    void paymentFailThenReturnErrorMessage() {
        paymentClientStub.setErrorCase(ALREADY_PROCESSED_PAYMENT);
        PaymentService paymentService = new PaymentService(paymentRepository, paymentClientStub);
        PaymentHistoryCreationContent paymentHistoryCreationContent = new PaymentHistoryCreationContent("asdf", "asdf",
                "asdf", 1000);
        assertThatThrownBy(() -> paymentService.savePayment(paymentHistoryCreationContent)).isInstanceOf(
                        PaymentException.class)
                .hasMessage(ALREADY_PROCESSED_PAYMENT);
    }
}
