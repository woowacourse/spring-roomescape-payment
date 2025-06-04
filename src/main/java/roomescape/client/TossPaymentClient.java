package roomescape.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.Payment;
import roomescape.service.dto.PaymentConfirmDto;

import java.util.Base64;

@Component
public class TossPaymentClient implements PaymentClient{

    private static final String PAYMENT_CONFIRM_URL = "/v1/payments/confirm";
    private static final String BASIC = "Basic ";

    @Value("${toss.payment.confirm.secretKey}")
    private String PAYMENT_CONFIRM_SECRET_KEY;

    @Qualifier("tossRestClient")
    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Payment confirmPayment(PaymentConfirmDto requestDto) {
        return restClient.post()
                .uri(PAYMENT_CONFIRM_URL)
                .header(HttpHeaders.AUTHORIZATION, BASIC +
                        Base64.getEncoder().encodeToString(PAYMENT_CONFIRM_SECRET_KEY.getBytes()))
                .body(requestDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Payment.class);
    }
}
