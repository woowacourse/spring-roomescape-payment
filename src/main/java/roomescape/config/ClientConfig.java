package roomescape.config;

import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    private final TossConfigProperties properties;

    public ClientConfig(final TossConfigProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestClient createPaymentClient() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(properties.connectTimeout());
        factory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Authorization", String.format("%s %s", "Basic", Base64.getEncoder()
                        .encodeToString(properties.secretKey().getBytes())))
                .requestFactory(factory)
                .build();
    }
}
