package roomescape.config;

import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    private static final int READ_TIMEOUT = 60_000;
    private static final int CONNECT_TIMEOUT = 5_000;
    private static final String baseUrl = "https://api.tosspayments.com";
    private static final String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";

    @Bean
    public RestClient createPaymentClient() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", String.format("%s %s", "Basic", Base64.getEncoder()
                        .encodeToString(secretKey.getBytes())))
                .requestFactory(factory)
                .build();
    }
}
