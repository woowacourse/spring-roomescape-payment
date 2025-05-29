package roomescape.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.thirdparty.PaymentRestClient;

@Configuration
public class RestClientConfig {

    @Value("${toss.payment.api.base-url}")
    private String paymentApiBaseUrl;

    @Bean
    public PaymentRestClient paymentRestClient() {
        return new PaymentRestClient(
                RestClient.builder()
                        .baseUrl(paymentApiBaseUrl)
                        .build());
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
