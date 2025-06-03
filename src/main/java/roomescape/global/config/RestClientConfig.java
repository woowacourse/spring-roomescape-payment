package roomescape.global.config;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;


@Configuration
public class RestClientConfig {

    private final String secretKey;
    private final String baseUrl;
    private final int connectTimeout;
    private final int readTimeout;

    public RestClientConfig(@Value("${toss.payment.secret-key}") String secretKey, @Value("${toss.payment.base-url}") String baseUrl, @Value("${toss.payment.connect-timeout}") int connectTimeout, @Value("${toss.payment.read-timeout}") int readTimeout) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        String basicAuthValue = encodeBasicAuth(secretKey);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthValue)
                .build();
    }

    private String encodeBasicAuth(String secretKey) {
        String auth = secretKey + ":";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }
}
