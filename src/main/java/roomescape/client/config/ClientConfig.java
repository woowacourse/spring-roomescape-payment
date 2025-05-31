package roomescape.client.config;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.client.TossPaymentsClient;

@Configuration
public class ClientConfig {

    private static final String TOSS_PAYMENTS_BASE_URL = "https://api.tosspayments.com/v1/payments/";

    private static final int CONNECTION_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 5000;

    private final String secretKey;

    public ClientConfig(@Value("${toss.payments.secret-key}") final String secretKey) {
        this.secretKey = secretKey;
    }

    @Bean
    public TossPaymentsClient tossRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        return new TossPaymentsClient(
                RestClient.builder()
                        .baseUrl(TOSS_PAYMENTS_BASE_URL)
                        .requestFactory(requestFactory)
                        .defaultHeader("Authorization", getBasicAuthorizationValue())
                        .build()
        );
    }

    private String getBasicAuthorizationValue() {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }
}
