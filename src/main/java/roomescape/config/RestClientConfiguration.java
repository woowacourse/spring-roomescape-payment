package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.config.dto.RestClientErrorResponse;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

@Configuration
public class RestClientConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(200);
        factory.setReadTimeout(30000);

        return restClientBuilder()
                .requestFactory(factory)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    RestClientErrorResponse restClientErrorResponse = objectMapper.readValue(res.getBody(), RestClientErrorResponse.class);
                    if (restClientErrorResponse.isInvisibleError()) {
                        throw new PaymentConfirmServerException(restClientErrorResponse.getMessage());
                    }
                    throw new PaymentConfirmClientException(restClientErrorResponse.getMessage());
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {
                    RestClientErrorResponse restClientErrorResponse = objectMapper.readValue(res.getBody(), RestClientErrorResponse.class);
                    throw new PaymentConfirmClientException(restClientErrorResponse.getMessage());
                })
                .build();
    }
}
