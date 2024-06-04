package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentErrorHandler;

@Configuration
public class PaymentRestClientConfig {

    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final int CONNECT_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 30000;

    @Bean
    public PaymentErrorHandler paymentErrorHandler() {
        return new PaymentErrorHandler();
    }

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        return RestClient.builder()
                .baseUrl(BASE_URL)
                .requestFactory(requestFactory)
                .build();
    }
}
