package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.service.payment.PaymentRestClient;

@Configuration
public class RestClientConfig {

    public static final int CONNECTION_TIMEOUT_THRESHOLD = 3000;
    public static final int READ_TIMEOUT_THRESHOLD = 3000;
    public static final String TEST_KEY_DELIMITER = ":";

    @Value("${tosspay.secret_key}")
    private String secretKey;

    @Bean
    public PaymentRestClient paymentRestClient() {
        return new PaymentRestClient(buildRestClient(), new ObjectMapper(), generateHttpHeaders());
    }

    private RestClient buildRestClient() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(Duration.ofMillis(CONNECTION_TIMEOUT_THRESHOLD));
        clientHttpRequestFactory.setReadTimeout(Duration.ofMillis(READ_TIMEOUT_THRESHOLD));
        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .requestFactory(clientHttpRequestFactory)
                .build();
    }

    private HttpHeaders generateHttpHeaders() {
        byte[] secretKeyBytes = (secretKey + TEST_KEY_DELIMITER).getBytes(StandardCharsets.UTF_8);
        String header = Base64.getEncoder()
                .encodeToString(secretKeyBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + header);
        return headers;
    }

}
