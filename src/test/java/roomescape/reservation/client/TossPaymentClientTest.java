package roomescape.reservation.client;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.client.ExpectedCount;

import roomescape.reservation.BaseTest;
import roomescape.reservation.dto.PaymentApprovalRequest;

class TossPaymentClientTest extends BaseTest {

    @Autowired
    private TossPaymentClient paymentClient;

    @Test
    void approvePayment() {
        // given
        server.reset();
        server.expect(ExpectedCount.once(), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess());
        PaymentApprovalRequest request = new PaymentApprovalRequest("paymentKey", "orderId", 1000L);

        // when
        paymentClient.approvePayment(request);

        // then
        server.verify();
    }
}
