package roomescape.reservation.client;

import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class TossPaymentsClient {

    private String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private final RestClient restClient;

    public TossPaymentsClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentsConfirmResponse confirmPayments(final PaymentsConfirmRequest request) {
        PaymentsConfirmResponse response = restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", getBasicAuthorizationValue())
                .body(request)
                .retrieve()
                .body(PaymentsConfirmResponse.class);

        return response;
    }

    private String getBasicAuthorizationValue() {
        return "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
    }
}
