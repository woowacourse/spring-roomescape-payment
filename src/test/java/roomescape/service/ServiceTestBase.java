package roomescape.service;

import static org.mockito.ArgumentMatchers.any;
import static roomescape.Fixture.TEST_ORDER_AMOUNT;
import static roomescape.Fixture.TEST_ORDER_ID;
import static roomescape.Fixture.TEST_PAYMENT_KEY;
import static roomescape.Fixture.TEST_PAYMENT_TYPE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import roomescape.domain.payment.Payment;
import roomescape.service.payment.TossPaymentClient;
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
        Mockito.when(restClient.confirm(any())).thenReturn(new PaymentResult(TEST_ORDER_AMOUNT, TEST_ORDER_ID, TEST_PAYMENT_KEY, TEST_PAYMENT_TYPE));
        Mockito.doNothing().when(restClient).cancel(any(Payment.class));
    }
}
