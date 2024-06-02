package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    private static final int CONNECT_TIMEOUT_MILLISECONDS = 60_000;
    private static final int CONNECTION_REQUEST_TIMEOUT_MILLISECONDS = 30_000;

    @Bean
    public RestClient restClient() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT_MILLISECONDS);
        clientHttpRequestFactory.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MILLISECONDS);
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory)
                .build();
    }
}
