package roomescape.common;

import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.NewPayment;
import roomescape.payment.domain.PGCompany;
import roomescape.payment.domain.PaymentCancelInfo;
import roomescape.payment.domain.PaymentCancelResult;
import roomescape.payment.domain.PaymentClient;

import java.util.concurrent.CompletableFuture;

public class StubPaymentClient implements PaymentClient {

    @Override
    public ConfirmedPayment confirm(NewPayment newPayment) {
        return new ConfirmedPayment("paymentKey", "orderId", 10, PGCompany.TOSS);
    }

    @Override
    public CompletableFuture<PaymentCancelResult> cancel(PaymentCancelInfo paymentCancelInfo) {
        return CompletableFuture.completedFuture(new PaymentCancelResult("CANCELED"));
    }
}
