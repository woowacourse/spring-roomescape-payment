package roomescape.payment.infrastructure.toss.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.provider.RestClientProvider;

@Component
public class TossRestClient {
    private final RestClient restClient;

    public TossRestClient(RestClientProvider restClientProvider,
                          @Value("${toss.baseUrl}") String baseUrl,
                          @Value("${toss.secretKey}") String secretKey) {
        this.restClient = restClientProvider.createRestClient(baseUrl, secretKey);
    }

    public RestClient getRestClient() {
        return restClient;
    }
}
