package roomescape.payment.domain;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface PaymentClient {
    ConfirmedPayment confirm(NewPayment newPayment);

    @Async
    CompletableFuture<PaymentCancelResult> cancel(PaymentCancelInfo paymentCancelInfo);
}
