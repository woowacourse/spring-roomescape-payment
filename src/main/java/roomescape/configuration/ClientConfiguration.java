package roomescape.configuration;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.utility.payment.PaymentClient;
import roomescape.utility.payment.TossPaymentClient;

@Configuration
public class ClientConfiguration {

    @Bean
    public PaymentClient paymentClient(
            @Value("${toss_payment_base_url}") String paymentUrl,
            @Value("${toss_payment_secret_key}") String secretKey,
            @Value("${toss_payment_authorization_url}") String paymentAuthorizationUrl
    ) {
        RestClient restClient = RestClient.builder()
                .baseUrl(paymentUrl)
                .requestFactory(simpleClientHttpRequestFactory())
                .defaultHeaders(headers -> headers.set("Content-Type", "application/json"))
                .build();
        return new TossPaymentClient(restClient, secretKey, paymentAuthorizationUrl);
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(30));
        return requestFactory;
    }
}
