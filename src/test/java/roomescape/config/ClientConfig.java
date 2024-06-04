package roomescape.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.client.FakePaymentClient;
import roomescape.client.PaymentRestClient;

@TestConfiguration
public class ClientConfig {
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public PaymentRestClient paymentRestClient() {
        return new FakePaymentClient(objectMapper);
    }
}
