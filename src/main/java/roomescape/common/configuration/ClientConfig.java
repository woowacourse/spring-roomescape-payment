package roomescape.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    private final int CONNECT_TIME_LIMIT = 3000;
    private final int READ_TIME_LIMIT = 7000;

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIME_LIMIT);
        requestFactory.setReadTimeout(READ_TIME_LIMIT);

        return RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .requestFactory(requestFactory)
            .build();
    }
}
