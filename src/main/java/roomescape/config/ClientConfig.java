package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.client.payment.service.PaymentClient;
import roomescape.util.LoggerUtil;

@Tag(name = "결제 요청 Client를 위한 설정", description = "결제 요청 Client가 실행될 수 있게 설정하고 요청, 응답 로그를 남긴다.")
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
        log.info("[토스 api 요청 정보] HTTP method: {}, URL: {}", request.getMethod(), request.getURI());
        if (request.getMethod().equals(HttpMethod.POST)) {
            log.info("[토스 api 사용자 요청] body: {}", new String(body, StandardCharsets.UTF_8));
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.info("[토스 api 상태코드] code: {}", response.getStatusCode());
    }
}


