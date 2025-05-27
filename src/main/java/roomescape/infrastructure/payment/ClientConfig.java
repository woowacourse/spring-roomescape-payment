package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public TossPaymentClient tossPaymentClient() {
        return new TossPaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                        .build(),
                objectMapper
        );
    }
}
