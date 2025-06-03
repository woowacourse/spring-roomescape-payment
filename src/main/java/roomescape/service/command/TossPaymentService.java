package roomescape.service.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.client.TossPaymentClient;
import roomescape.domain.payment.Payment;
import roomescape.dto.reservation.PaymentConfirmDto;

import java.util.Base64;

@Service
public class TossPaymentService implements PaymentService{

    private static final String PAYMENT_CONFIRM_URL = "/v1/payments/confirm";
    private static final String BASIC = "Basic ";

    @Value("${toss.payment.confirm.secretKey}")
    private String PAYMENT_CONFIRM_SECRET_KEY;

    private final RestClient restClient;

    public TossPaymentService(TossPaymentClient tossPaymentClient) {
        this.restClient = tossPaymentClient.buildRestClient();
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
