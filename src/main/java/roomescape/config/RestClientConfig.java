package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.service.payment.PaymentRestClient;

@Configuration
public class RestClientConfig {

    @Value("${tosspay.secret_key}")
    private String secretKey;

    @Bean
    public PaymentRestClient paymentRestClient() {
        return new PaymentRestClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments").build(),
                new ObjectMapper(),
                generateHttpHeaders()
        );
    }

    private HttpHeaders generateHttpHeaders() {
        String header = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + header);
        return headers;
    }
}
