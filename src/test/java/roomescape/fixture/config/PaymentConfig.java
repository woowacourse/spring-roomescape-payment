package roomescape.fixture.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentDomainService;
import roomescape.payment.infrastructure.toss.TossPaymentClient;

@TestConfiguration
public class PaymentConfig {

    private final String confirmUrl;
    private final String secretKey;

    public PaymentConfig(
            @Value("${payment.toss.confirm-url}") final String confirmUrl,
            @Value("${payment.toss.secret-key}") final String secretKey
    ) {
        this.confirmUrl = confirmUrl;
        this.secretKey = secretKey;
    }

    @Bean
    public PaymentDomainService paymentDomainService(
            final PaymentClient paymentClient
    ) {
        return new PaymentDomainService(paymentClient);
    }

    @Bean
    public PaymentClient paymentClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(30));

        return new TossPaymentClient(
                confirmUrl,
                secretKey,
                RestClient.builder()
                        .requestFactory(requestFactory)
                        .baseUrl(confirmUrl)
                        .build()
        );
    }
}
