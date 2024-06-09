package roomescape.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import roomescape.config.properties.TossPaymentConfigProperties;

@Configuration
public class PaymentRestClientConfig {

    private final TossPaymentConfigProperties tossProperties;

    public PaymentRestClientConfig(TossPaymentConfigProperties tossProperties) {
        this.tossProperties = tossProperties;
    }

    @Bean
    public RestClient.Builder tossRestClientBuilder() {
        return RestClient.builder()
                .baseUrl(tossProperties.baseUri())
                .defaultHeaders(this::setTossHeaders)
                .requestFactory(tossRequestFactory());
    }

    private void setTossHeaders(HttpHeaders headers) {
        headers.setBasicAuth(tossAuthorization());
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private ClientHttpRequestFactory tossRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(tossProperties.connectTimeout()));
        factory.setReadTimeout(Duration.ofSeconds(tossProperties.readTimeout()));
        return factory;
    }

    private String tossAuthorization() {
        String secretKeyWithoutPassword = tossProperties.secret() + ":";
        Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(secretKeyWithoutPassword.getBytes(StandardCharsets.UTF_8));
    }
}
