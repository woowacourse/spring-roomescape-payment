package roomescape.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.client.dto.PaymentConfirmResultDto;
import roomescape.service.dto.PaymentConfirmDto;

import java.util.Base64;

@Component
public class TossPaymentClient implements PaymentClient{

    private static final String PAYMENT_CONFIRM_URL = "/v1/payments/confirm";
    private static final String BASIC = "Basic ";

    private final String secretKey;

    @Qualifier("tossRestClient")
    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient,
                             @Value("${toss.payment.confirm.secretKey}") String secretKey) {
        this.restClient = restClient;
        this.secretKey = secretKey;
    }

    @Override
    public PaymentConfirmResultDto confirmPayment(PaymentConfirmDto requestDto) {
        return restClient.post()
                .uri(PAYMENT_CONFIRM_URL)
                .header(HttpHeaders.AUTHORIZATION, BASIC +
                        Base64.getEncoder().encodeToString(secretKey.getBytes()))
                .body(requestDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(PaymentConfirmResultDto.class);
    }
}
