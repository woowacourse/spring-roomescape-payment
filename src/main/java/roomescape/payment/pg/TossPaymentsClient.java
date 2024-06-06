package roomescape.payment.pg;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TossPaymentsClient {
    private final TossPaymentsErrorHandler errorHandler;
    private final RestClient restClient;
    private final String confirmApiPath;
    private final String findApiPath;

    public TossPaymentsClient(TossPaymentsErrorHandler errorHandler,
                              @Qualifier(value = "tossRestClientBuilder") RestClient.Builder restClientBuilder,
                              @Value("${pg.toss.confirm-api-path}") String confirmApiPath,
                              @Value("${pg.toss.find-api-path}") String findApiPath) {
        this.errorHandler = errorHandler;
        this.restClient = restClientBuilder.build();
        this.confirmApiPath = confirmApiPath;
        this.findApiPath = findApiPath;
    }

    public TossPaymentsPayment findBy(String paymentKey) {
        return restClient.get()
                .uri(findApiPath + "/{paymentKey}", paymentKey)
                .retrieve()
                .onStatus(errorHandler)
                .toEntity(TossPaymentsPayment.class)
                .getBody();
    }

    public void confirm(TossPaymentsConfirmRequest request) {
        restClient.post()
                .uri(confirmApiPath)
                .body(request)
                .retrieve()
                .onStatus(errorHandler)
                .toBodilessEntity();
    }
}
