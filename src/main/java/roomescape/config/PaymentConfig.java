package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.toss.TossPaymentWithRestClient;
import roomescape.infrastructure.payment.toss.exception.PaymentExceptionHandler;

@Configuration
@RequiredArgsConstructor
public class PaymentConfig {

    private final ObjectMapper mapper;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEME = "Basic ";

    @Value("${security.toss.payment.secret-key}")
    private String secretKey;

    @Bean
    public TossPaymentWithRestClient tossPaymentWithRestClient(RestClient.Builder builder) {
        return new TossPaymentWithRestClient(builder
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultStatusHandler(new PaymentExceptionHandler(mapper))
                .requestInterceptor((request, body, execution) -> {
                    if (request.getURI().getPath().contains("/confirm")) {
                        request.getHeaders().add(AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME + encodeSecretKey());
                    }
                    return execution.execute(request, body);
                })
                .requestFactory(createRequestFactory())
                .build());
    }

    private static SimpleClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(30));
        return requestFactory;
    }

    private String encodeSecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
