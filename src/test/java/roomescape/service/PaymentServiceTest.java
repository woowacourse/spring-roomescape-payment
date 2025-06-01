package roomescape.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.exception.PaymentException;
import roomescape.repository.PaymentHistoryRepository;
import roomescape.repository.PaymentResultRepository;
import roomescape.utility.PaymentClientStub;

@DataJpaTest
class PaymentServiceTest {

    private static final String ALREADY_PROCESSED_PAYMENT = "이미 처리된 결제 입니다.";

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    private final PaymentClientStub paymentClientStub = new PaymentClientStub();

    @Autowired
    private PaymentResultRepository paymentResultRepository;

    @Test
    void paymentFailThenReturnErrorMessage() {
        paymentClientStub.setErrorCase(ALREADY_PROCESSED_PAYMENT);
        PaymentService paymentService = new PaymentService(paymentHistoryRepository, paymentClientStub,
                paymentResultRepository);
        PaymentHistoryCreationContent paymentHistoryCreationContent = new PaymentHistoryCreationContent("asdf", "asdf",
                "asdf", 1000);
        assertThatThrownBy(() -> paymentService.pay(paymentHistoryCreationContent)).isInstanceOf(
                        PaymentException.class)
                .hasMessage(ALREADY_PROCESSED_PAYMENT);
    }
}
