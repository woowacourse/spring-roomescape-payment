package roomescape.application.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.exception.payment.PaymentException;
import roomescape.util.Base64Utils;

@Component
public class PaymentClient {
    private static final String BASIC_AUTH_FORMAT = "Basic %s";

    private final RestClient client;
    private final ResponseErrorHandler handler;
    private final String authorizationSecret;

    public PaymentClient(RestClient.Builder builder,
                         ResponseErrorHandler handler,
                         @Value("${payment.secret}") String secret) {
        this.authorizationSecret = String.format(
                BASIC_AUTH_FORMAT,
                Base64Utils.encode(secret + ":")
        );
        this.client = builder.build();
        this.handler = handler;
    }

    public Payment requestPurchase(PaymentRequest request) {
        Payment payment = client.post()
                .uri("/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, authorizationSecret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(handler)
                .body(Payment.class);

        if (payment == null) {
            throw new PaymentException("결제에 실패했습니다.");
        }
        return payment;
    }
}
