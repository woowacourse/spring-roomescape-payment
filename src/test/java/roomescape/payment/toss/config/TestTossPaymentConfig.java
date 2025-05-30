package roomescape.payment.toss.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.toss.interceptor.TossPaymentResponseInterceptor;

@TestConfiguration
public class TestTossPaymentConfig {

    private static final String COLON = ":";

    private final ObjectMapper objectMapper;
    private final String token;
    private final int connectTimeoutMs;
    private final String url;

    public TestTossPaymentConfig(ObjectMapper objectMapper,
                                 TossPaymentConfigProperties tossPaymentConfigProperties) {
        this.objectMapper = objectMapper;
        this.token = tossPaymentConfigProperties.getToken();
        this.connectTimeoutMs = tossPaymentConfigProperties.getConnectionTimeoutMs();
        this.url = tossPaymentConfigProperties.getUrl();
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);

        return RestClient.builder()
                .baseUrl(url)
                .requestInterceptor(new TossPaymentResponseInterceptor(objectMapper))
                .requestFactory(requestFactory)
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString(((token + COLON).getBytes())));
    }
}
