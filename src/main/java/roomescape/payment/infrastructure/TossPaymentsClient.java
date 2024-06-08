package roomescape.payment.infrastructure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.NewPayment;
import roomescape.payment.domain.PaymentCancelInfo;
import roomescape.payment.domain.PaymentCancelResult;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.exception.PaymentServerException;

import java.util.concurrent.CompletableFuture;

@Component
public class TossPaymentsClient implements PaymentClient {
    private final RestClient restClient;
    private final PaymentClientErrorHandler errorHandler;

    public TossPaymentsClient(@Qualifier("tossPaymentsClientBuilder") RestClient.Builder builder,
                              PaymentClientErrorHandler errorHandler) {
        this.restClient = builder.build();
        this.errorHandler = errorHandler;
    }

    @Override
    public ConfirmedPayment confirm(NewPayment newPayment) {
        return restClient.post()
                .uri("/confirm")
                .body(newPayment)
                .retrieve()
                .onStatus(errorHandler)
                .body(ConfirmedPayment.class);
    }

    @Override
    public CompletableFuture<PaymentCancelResult> cancel(PaymentCancelInfo paymentCancelInfo) {
        try {
            String uri = String.format("/%s/cancel", paymentCancelInfo.paymentKey());
            PaymentCancelResult paymentCancelResult = restClient.post()
                    .uri(uri)
                    .body(paymentCancelInfo)
                    .retrieve()
                    .onStatus(errorHandler)
                    .body(PaymentCancelResult.class);
            validateCanceledPayment(paymentCancelResult);
            return CompletableFuture.completedFuture(paymentCancelResult);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private void validateCanceledPayment(PaymentCancelResult paymentCancelResult) {
        if (paymentCancelResult == null) {
            throw new PaymentServerException("결제 취소가 실패했습니다.");
        }
        if (!paymentCancelResult.isCorrectStatus()) {
            throw new PaymentServerException("결제 취소가 실패했습니다.");
        }
    }
}
