package roomescape.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {


    public static final int CONNECT_TIMEOUT_MILLISECONDS = 10_000;
    public static final int READ_TIMEOUT_MILLISECONDS = 30_000;

    @Bean
    public RestClient restClient() {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MILLISECONDS);
        factory.setReadTimeout(READ_TIMEOUT_MILLISECONDS);

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .requestFactory(factory)
                .build();
    }
}
