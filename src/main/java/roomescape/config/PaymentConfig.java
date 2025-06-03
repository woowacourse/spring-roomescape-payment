package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
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
        RestClient restClient = createRestClient(builder);

        return createTossPaymentWithRestClient(restClient);
    }

    public TossPaymentWithRestClient createTossPaymentWithRestClient(RestClient client) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(client))
                .build();

        return factory.createClient(TossPaymentWithRestClient.class);
    }

    private RestClient createRestClient(Builder builder) {
        return builder
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultStatusHandler(new PaymentExceptionHandler(mapper))
                .requestInterceptor((request, body, execution) -> {
                    if (request.getURI().getPath().contains("/confirm")) {
                        request.getHeaders().add(AUTHORIZATION_HEADER, AUTHORIZATION_SCHEME + encodeSecretKey());
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    private String encodeSecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
