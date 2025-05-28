package roomescape.config;

import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient paymentClient() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(5_000);
        factory.setReadTimeout(5_000);

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", String.format("%s %s", "Basic", Base64.getEncoder()
                        .encodeToString("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:".getBytes())))
                .requestFactory(factory)
                .build();
    }
}
