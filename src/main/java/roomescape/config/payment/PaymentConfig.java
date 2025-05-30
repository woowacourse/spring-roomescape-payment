package roomescape.config.payment;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.utility.PaymentClient;
import roomescape.utility.TossPaymentClient;

@Configuration
@Profile("!test")
public class PaymentConfig {

    @Bean
    public PaymentClient paymentClient(
            @Value("${toss_payment_url}") String paymentUrl,
            @Value("${toss_payment_secret_key}") String secretKey,
            @Value("${toss_confirm_server_url}") String confirmServerUrl
    ) {
        RestClient restClient = RestClient.builder()
                .baseUrl(paymentUrl)
                .requestFactory(simpleClientHttpRequestFactory())
                .defaultHeaders(headers -> {
                    headers.set("Content-Type", "application/json");
                })
                .build();

        return new TossPaymentClient(restClient, secretKey, confirmServerUrl);
    }

    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(30));
        return requestFactory;
    }
}
