package roomescape.payment;

import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final String TEST_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";

    @Bean
    public PaymentRestClient getPaymentRestClient() {
        return new PaymentRestClient(RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", getEncodedKey())
                .defaultHeader("Content-Type", "application/json")
                .build());
    }

    private String getEncodedKey() {
        StringBuilder sb = new StringBuilder();
        sb.append("Basic ");
        sb.append(Base64.getEncoder().encodeToString(TEST_KEY.getBytes()));
        return sb.toString();
    }
}
