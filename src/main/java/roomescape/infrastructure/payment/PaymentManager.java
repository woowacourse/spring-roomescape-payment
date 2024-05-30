package roomescape.infrastructure.payment;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.service.request.PaymentApproveDto;

@Component
public class PaymentManager {

    private final PaymentAuthorizationGenerator paymentAuthorizationGenerator;
    private final RestClient restClient;

    public PaymentManager(PaymentAuthorizationGenerator paymentAuthorizationGenerator, RestClient restClient) {
        this.paymentAuthorizationGenerator = paymentAuthorizationGenerator;
        this.restClient = restClient;
    }

    public void approve(PaymentApproveDto request) {
        String authorizations = paymentAuthorizationGenerator.createAuthorizations();

        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
