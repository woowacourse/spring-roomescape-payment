package roomescape.order;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.order.dto.PaymentConfirmRequest;

import java.util.Base64;

@Component
@AllArgsConstructor
public class PaymentClient {

    private static final String TEST_WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private static final String ENCODED_SECRET_KEY = Base64.getEncoder().encodeToString(TEST_WIDGET_SECRET_KEY.getBytes());
    private static final String URL_PREFIX = "https://api.tosspayments.com/v1/payments";
    private final RestClient restClient;

    public void confirm(final PaymentConfirmRequest request) {
        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri(URL_PREFIX + "/confirm")
                    .header("Authorization", "Basic " + ENCODED_SECRET_KEY)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new IllegalStateException("Payment confirmation failed");
            }
        } catch (RuntimeException e) {
            
        }
    }
}
