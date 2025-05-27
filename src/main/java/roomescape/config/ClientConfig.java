package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentClientService;

import java.util.Base64;

@Configuration
public class ClientConfig {
    @Bean
    public PaymentClientService paymentClient() {
        return new PaymentClientService(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                        .defaultHeader("Authorization",String.format("%s %s","Basic",Base64.getEncoder().encodeToString("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw 6:".getBytes())))
                        .build()
        );
    }
}
