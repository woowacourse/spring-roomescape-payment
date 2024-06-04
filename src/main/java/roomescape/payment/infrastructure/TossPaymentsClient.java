package roomescape.payment.infrastructure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;

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
    public PaymentConfirmResponse confirm(PaymentConfirmRequest request) {
        return restClient.post()
                .uri("/confirm")
                .body(request)
                .retrieve()
                .onStatus(errorHandler)
                .body(PaymentConfirmResponse.class);
    }
}
