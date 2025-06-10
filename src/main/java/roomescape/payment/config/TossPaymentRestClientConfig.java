package roomescape.payment.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class TossPaymentRestClientConfig {

    @Value("${toss.confirm.secret-key}")
    private String tossSecretKey;

    @Value("${restclient.connection-timeout:3000}")
    private int connectionTimeout;

    @Value("${restclient.read-timeout:5000}")
    private int readTimeout;

    @Bean
    public RestClient tossPaymentRestClient(RestClient.Builder restClientBuilder) {
        String base64EncodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizationHeader = "Basic " + base64EncodedKey;

        return restClientBuilder
                .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                .defaultHeader("Authorization", authorizationHeader)
                .defaultHeader("Content-Type", APPLICATION_JSON_VALUE)
                .requestFactory(simpleClientHttpRequestFactory())
                .build();
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return requestFactory;
    }
}
