package roomescape.payment.toss.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.toss.interceptor.TossPaymentResponseInterceptor;
import roomescape.payment.toss.service.TossPaymentClient;

@Configuration
@ConfigurationPropertiesScan
public class TossPaymentConfig {

    private static final String COLON = ":";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Authorization";
    private static final String BASIC_AUTHENTICATION_PREFIX = "Basic ";

    private final ObjectMapper objectMapper;
    private final String token;
    private final int connectTimeoutMs;
    private final String paymentUrl;

    public TossPaymentConfig(ObjectMapper objectMapper,
                             TossPaymentConfigProperties tossPaymentConfigProperties) {
        this.objectMapper = objectMapper;
        this.token = tossPaymentConfigProperties.getToken();
        this.connectTimeoutMs = tossPaymentConfigProperties.getConnectionTimeoutMs();
        this.paymentUrl = tossPaymentConfigProperties.getUrl();
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
                .baseUrl(paymentUrl)
                .requestInterceptor(new TossPaymentResponseInterceptor(objectMapper))
                .requestFactory(requestFactory)
                .defaultHeader(AUTHORIZATION_HEADER_PREFIX, BASIC_AUTHENTICATION_PREFIX + Base64.getEncoder()
                        .encodeToString(((token + COLON).getBytes())))
                .build();
    }
}
