package roomescape.payment.pg;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import roomescape.global.exception.ViolationException;
import roomescape.payment.application.ProductPayRequest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static roomescape.TestFixture.PRODUCT_PAY_REQUEST;

class TossPaymentGatewayTest {
    private final TossPaymentsClient client = Mockito.mock(TossPaymentsClient.class);
    private final TossPaymentGateway tossPaymentGateway = new TossPaymentGateway(client);

    @Test
    @DisplayName("승인 정보가 잘못된 경우, 결제를 승인할 수 없다.")
    void processAfterPaid() {
        TossPaymentsPayment payment = Mockito.mock(TossPaymentsPayment.class);
        BDDMockito.when(payment.verify(any()))
                .thenReturn(false);
        BDDMockito.when(client.findBy(any()))
                .thenReturn(payment);
        ProductPayRequest request = PRODUCT_PAY_REQUEST("paymentKey", "orderId");

        assertThatThrownBy(() -> tossPaymentGateway.processAfterPaid(request))
                .isInstanceOf(ViolationException.class)
                .hasMessage("올바른 결제 정보를 입력해주세요.");
    }
}
