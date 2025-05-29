package roomescape.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

import java.util.Set;

@Configuration
public class RestClientConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    private static final Set<String> INVISIBLE_CLIENT_ERROR_CODE = Set.of(
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    );

//    @Autowired
//    HttpComponentsClientHttpRequestFactory factory;
//
//    @Bean
//    public HttpComponentsClientHttpRequestFactory factory() {
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//       factory.setConnectTimeout(10000);
//       factory.setReadTimeout(30000);
//        return factory;
//    }

    @Bean
    public RestClient restClient() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(200);
        factory.setReadTimeout(30000);

        return RestClient.builder()
                .requestFactory(factory)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String message = root.path("message").asText();
                    String code = root.path("code").asText();
                    if (INVISIBLE_CLIENT_ERROR_CODE.contains(code)) {
                        throw new PaymentConfirmServerException(message);
                    }
                    throw new PaymentConfirmClientException(message);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String message = root.path("message").asText();
                    throw new PaymentConfirmServerException(message);
                })
                .build();
    }
}
