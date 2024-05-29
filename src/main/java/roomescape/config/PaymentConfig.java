package roomescape.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.dto.response.reservation.TossExceptionResponse;
import roomescape.exception.PaymentException;
import roomescape.service.PaymentService;

@Configuration
public class PaymentConfig {
    @Value("${payment.toss.secret-key}")
    private String widgetSecretKey;

    @Bean
    public PaymentService paymentService() {
        byte[] encodedBytes = Base64.getEncoder().encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorization = "Basic " + new String(encodedBytes);

        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorization)
                .defaultStatusHandler(HttpStatusCode::isError, ((request, response) -> {
                    throw new PaymentException((HttpStatus) response.getStatusCode(), getTossExceptionResponse(response));
                }))
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(PaymentService.class);
    }

    private TossExceptionResponse getTossExceptionResponse(ClientHttpResponse response) throws IOException {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(response.getBody(), TossExceptionResponse.class);
    }
}
