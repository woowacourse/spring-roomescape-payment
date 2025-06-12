package roomescape.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.toss.TossPaymentWithRestClient;
import roomescape.infrastructure.payment.toss.exception.PaymentExceptionHandler;

@TestConfiguration
@RequiredArgsConstructor
public class TestPaymentConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEME = "Basic ";
    public static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    private final ObjectMapper mapper;

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
                .build());
    }

    private String encodeSecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
