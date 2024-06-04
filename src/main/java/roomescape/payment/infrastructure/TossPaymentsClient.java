package roomescape.payment.infrastructure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.NewPayment;
import roomescape.payment.domain.PaymentClient;

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
}
