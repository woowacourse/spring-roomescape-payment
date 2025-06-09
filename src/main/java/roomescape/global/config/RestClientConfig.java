package roomescape.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.reservation.external.toss.TossAuthToken;

@Configuration
public class RestClientConfig {

    @Value("${api.toss.secret-key}")
    private String tossSecretKey;

    @Value("${api.toss.uri}")
    private String tossUri;

    @Bean
    public RestClient tossRestClient() {
        TossAuthToken authToken = new TossAuthToken(tossSecretKey);

        return RestClient.builder()
                .baseUrl(tossUri)
                .defaultRequest(requestBuilder -> requestBuilder
                        .header(HttpHeaders.AUTHORIZATION, authToken.generateToken())
                )
                .build();
    }
}
