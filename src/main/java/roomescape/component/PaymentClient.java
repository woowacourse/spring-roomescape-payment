package roomescape.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.payment.TossPaymentError;
import roomescape.dto.payment.PaymentDto;
import roomescape.exception.PaymentException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class PaymentClient {

    private static final String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentClient(final RestClient restClient, final ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void confirm(final PaymentDto paymentDto) {
        restClient.post()
                .headers(httpHeaders -> httpHeaders.addAll(headers()))
                .body(paymentDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    final TossPaymentError tossPaymentError = objectMapper.readValue(response.getBody(), TossPaymentError.class);
                    throw new PaymentException(response.getStatusCode(), tossPaymentError.message());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    final TossPaymentError tossPaymentError = objectMapper.readValue(response.getBody(), TossPaymentError.class);
                    throw new PaymentException(response.getStatusCode(), tossPaymentError.message());
                })
                .toBodilessEntity();
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(basicAuthorization());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String basicAuthorization() {
        final String secretKeyWithoutPassword = widgetSecretKey + ":";
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(secretKeyWithoutPassword.getBytes(StandardCharsets.UTF_8));
    }
}
