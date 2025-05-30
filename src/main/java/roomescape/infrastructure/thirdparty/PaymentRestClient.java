package roomescape.infrastructure.thirdparty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.presentation.dto.request.PaymentProcessRequest;

@Component
public class PaymentRestClient {

    private final RestClient restClient;
    private final AuthHeaderGenerator authHeaderGenerator;

    @Value("${toss.payment.api.base-url}")
    private String paymentApiBaseUrl;

    @Value("${toss.payment.api.key}")
    private String tossKey;

    public PaymentRestClient(
            RestClient.Builder restClientBuilder,
            AuthHeaderGenerator authHeaderGenerator
    ) {
        this.restClient = restClientBuilder
                .baseUrl(paymentApiBaseUrl)
                .build();
        this.authHeaderGenerator = authHeaderGenerator;
    }

    public ResponseEntity<String> getPaymentResponse(PaymentProcessRequest request) {
        return restClient.post()
                .header(HttpHeaders.AUTHORIZATION, authHeaderGenerator.generateBasicAuthHeader(tossKey))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(String.class);
    }
}
