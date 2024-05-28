package roomescape.web.controller.api.payment;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.web.controller.request.PaymentApproveRequest;

@Component
public class PaymentManager {

    private final PaymentAuthorizationGenerator paymentAuthorizationGenerator;
    private final RestClient restClient;

    public PaymentManager(PaymentAuthorizationGenerator paymentAuthorizationGenerator) {
        this.paymentAuthorizationGenerator = paymentAuthorizationGenerator;
        this.restClient = RestClient.builder().build();
    }

    public void approve(PaymentApproveRequest request) {
        String authorizations = paymentAuthorizationGenerator.createAuthorizations();

        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
