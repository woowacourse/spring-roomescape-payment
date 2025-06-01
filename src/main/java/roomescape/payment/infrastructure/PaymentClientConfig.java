package roomescape.payment.infrastructure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class PaymentClientConfig {

    @Bean
    public TossRestClient tossRestClient(RestClient.Builder restClientBuilder, TossPaymentProperties tossPaymentProperties) {

        return new TossRestClient(restClientBuilder
                .baseUrl(tossPaymentProperties.getBaseUrl())
                .build(),
                tossPaymentProperties
        );
    }
}
