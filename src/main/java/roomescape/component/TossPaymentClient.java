package roomescape.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.payment.PaymentDto;
import roomescape.exception.TossPaymentsErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class TossPaymentClient {

    @Value("${toss.secret-key}")
    private String widgetSecretKey;

    private final RestClient restClient;
    private final TossPaymentsErrorHandler errorHandler;

    public TossPaymentClient(final RestClient restClient,
                             final TossPaymentsErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public void confirm(final PaymentDto paymentDto) {
        restClient.post()
                .uri("/confirm")
                .headers(httpHeaders -> httpHeaders.addAll(headers()))
                .body(paymentDto)
                .retrieve()
                .onStatus(errorHandler)
                .toBodilessEntity();
    }

    public void cancel(final PaymentDto paymentDto, final String cancelReason) {
        restClient.post()
                .uri("/" + paymentDto.paymentKey() + "/cancel")
                .headers(httpHeaders -> httpHeaders.addAll(headers()))
                .body(Map.of("cancelReason", cancelReason))
                .retrieve()
                .onStatus(errorHandler)
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
