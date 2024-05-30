package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.paymenthistory.domain.PaymentRestClient;

@Configuration
public class BeanConfiguration {

    private static final String TOSS_PAYMENT_URL = "https://api.tosspayments.com";

    @Value("${payment.secret-key}")
    private String secretKey;

    @Bean
    public PaymentRestClient paymentRestClient() {
        return new PaymentRestClient(RestClient.builder()
                .baseUrl(TOSS_PAYMENT_URL)
                .requestFactory(getClientHttpRequestFactory())
                .build(), secretKey);
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(5000);
        simpleClientHttpRequestFactory.setReadTimeout(30000);
        return simpleClientHttpRequestFactory;
    }
}
