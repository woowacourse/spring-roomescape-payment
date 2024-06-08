package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.client.payment.PaymentClient;
import roomescape.util.LoggerUtil;

@Configuration
public class ClientConfig {

    private static final String TOSS_PAYMENT_CONFIRM_URL = "https://api.tosspayments.com";

    private static final Logger log = LoggerUtil.getLogger(ClientConfig.class);

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public PaymentClient paymentClient(@Value("${payment.widget-secret-key}") String widgetSecretKey,
                                       RestClient restClient) {
        return new PaymentClient(widgetSecretKey, restClient, new ObjectMapper());
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .requestFactory(getClientHttpRequestFactory())
                .baseUrl(TOSS_PAYMENT_CONFIRM_URL)
                .requestInterceptor(
                        (request, body, execution) -> {
                            logRequest(request, body);
                            ClientHttpResponse response = execution.execute(request, body);
                            logResponse(response);
                            return response;
                        })
                .build();
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.error("[토스 api 요청 정보] HTTP method: {}, URL: {}", request.getMethod(), request.getURI());
        if (request.getMethod().equals(HttpMethod.POST)) {
            log.error("[토스 api 사용자 요청] body: {}", new String(body, StandardCharsets.UTF_8));
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.error("[토스 api 예외 상태코드] code: {}", response.getStatusCode());
    }
}


