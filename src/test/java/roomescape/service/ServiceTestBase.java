package roomescape.service;

import static org.mockito.ArgumentMatchers.any;
import static roomescape.Fixture.CANCEL_ERROR_KEY;
import static roomescape.Fixture.PAYMENT_ERROR_KEY;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import roomescape.domain.payment.Payment;
import roomescape.exception.PaymentException;
import roomescape.service.payment.TossPaymentClient;
import roomescape.service.payment.dto.PaymentRequest;
import roomescape.service.payment.dto.PaymentResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(SpringExtension.class)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql("/truncate.sql")
public abstract class ServiceTestBase {
    @MockBean
    protected TossPaymentClient restClient;

    @BeforeEach
    void setUpPaymentResult() {
        Mockito.when(restClient.confirm(any(PaymentRequest.class)))
                .thenAnswer(this::getMockedPaymentResult);
        Mockito.doAnswer(this::getMockedPaymentCancelResult).when(restClient).cancel(any(Payment.class));
    }

    private Object getMockedPaymentCancelResult(InvocationOnMock invocation) {
        Payment payment = invocation.getArgument(0);
        if (payment.getPaymentKey().equals(CANCEL_ERROR_KEY)) {
            throw new PaymentException("결제 취소 중 오류 발생");
        }
        return null;
    }

    private PaymentResult getMockedPaymentResult(InvocationOnMock invocation) {
        PaymentRequest argument = invocation.getArgument(0, PaymentRequest.class);
        if (argument.paymentType().isByAdmin()) {
            return new PaymentResult(BigDecimal.ZERO, "ADMIN", "ADMIN", "ADMIN");
        }
        if (argument.paymentKey().equals(PAYMENT_ERROR_KEY)) {
            throw new PaymentException("결제 중 오류 발생");
        }
        return new PaymentResult(argument.amount(), argument.orderId(), argument.paymentKey(), argument.paymentType().name());
    }
}
