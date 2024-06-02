package roomescape.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import roomescape.config.properties.PaymentClientProperties;
import roomescape.config.properties.TossPaymentClientProperties;
import roomescape.dto.response.reservation.TossExceptionResponse;
import roomescape.exception.PaymentException;

public class TossPaymentClientFactory implements PaymentClientFactory {
    private final PaymentClientProperties properties;

    public TossPaymentClientFactory(PaymentClientProperties properties) {
        this.properties = properties;
    }

    @Override
    public RestClient createPaymentClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .defaultHeader(HttpHeaders.AUTHORIZATION, createAuthorization())
                .defaultStatusHandler(HttpStatusCode::isError, ((request, response) -> {
                    throw new PaymentException((HttpStatus) response.getStatusCode(),
                            getTossExceptionResponse(response));
                }))
                .build();
    }

    private String createAuthorization() {
        byte[] encodedBytes = Base64.getEncoder().encode((properties.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    private TossExceptionResponse getTossExceptionResponse(ClientHttpResponse response) throws IOException {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(response.getBody(), TossExceptionResponse.class);
    }
}
