package roomescape.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.interceptor.TossPaymentResponseInterceptor;
import roomescape.payment.service.TossPaymentClient;

@Configuration
public class TossPaymentConfig {

    private static final String COLON = ":";
    private static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Authorization";
    private static final String BASIC_AUTHENTICATION_PREFIX = "Basic ";

    private final ObjectMapper objectMapper;
    private final String token;
    private final int connectTimeoutMs;

    public TossPaymentConfig(ObjectMapper objectMapper,
                             @Value("${payment.token}") String token,
                             @Value("${payment.connection-timeout}") int connectTimeoutMs) {
        this.objectMapper = objectMapper;
        this.token = token;
        this.connectTimeoutMs = connectTimeoutMs;
    }

    @Bean
    public TossPaymentClient paymentClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);

        RestClient client = getRestClient(requestFactory);

        return getTossPaymentClient(client);
    }

    private TossPaymentClient getTossPaymentClient(RestClient client) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(client))
                .build();

        return factory.createClient(TossPaymentClient.class);
    }

    private RestClient getRestClient(SimpleClientHttpRequestFactory requestFactory) {
        return RestClient.builder()
                .baseUrl(PAYMENT_URL)
                .requestInterceptor(new TossPaymentResponseInterceptor(objectMapper))
                .requestFactory(requestFactory)
                .defaultHeader(AUTHORIZATION_HEADER_PREFIX, BASIC_AUTHENTICATION_PREFIX + Base64.getEncoder()
                        .encodeToString(((token + COLON).getBytes())))
                .build();
    }
}
