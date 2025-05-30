package roomescape.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    public static final int CONNECT_TIMEOUT = 5_000;
    public static final int READ_TIMEOUT = 30 * 1_000;

    @Bean
    public RestClient.Builder restClient() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        return RestClient
                .builder()
                .requestFactory(requestFactory);
    }
}
