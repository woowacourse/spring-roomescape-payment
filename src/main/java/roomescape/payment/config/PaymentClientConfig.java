package roomescape.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.PaymentClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Configuration
public class PaymentClientConfig {

    @Value("${payment.secret.key}")
    private String secretKey;

    @Bean
    public PaymentClient getPaymentResolver() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        return new PaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                        .defaultHeader("Content-Type", "application/json")
                        .defaultHeader("Authorization", authorizations)
                        .requestInterceptor(loggingInterceptor())
                        .build()
        );
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("Payment API 요청: {} {} | Body: {}",
                    request.getMethod(),
                    request.getURI(),
                    new String(body, StandardCharsets.UTF_8));

            ClientHttpResponse response = execution.execute(request, body);

            log.debug("Payment API 응답: {}", response.getStatusCode());

            return response;
        };
    }
}
