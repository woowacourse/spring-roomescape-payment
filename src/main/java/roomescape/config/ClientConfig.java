package roomescape.config;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    private static final int READ_TIMEOUT = 60_000;
    private static final int CONNECT_TIMEOUT = 5_000;

    @Value("${toss.payment.secret-key}")
    private String SECRET_KEY;

    @Value("${toss.payment.base-url}")
    private String BASE_URL;

    @Bean
    public RestClient createPaymentClient() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);

        return RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", String.format("%s %s", "Basic", Base64.getEncoder()
                        .encodeToString(SECRET_KEY.getBytes())))
                .requestFactory(factory)
                .build();
    }
}
