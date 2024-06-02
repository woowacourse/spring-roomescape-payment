package roomescape.service;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentInfo;
import roomescape.dto.request.MemberReservationRequest;

@Service
public class PaymentService {

    @Value("${atto.ash.secret-key}")
    private String secretKey;
    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentInfo payment(MemberReservationRequest memberReservationRequest) {
        Long amount = memberReservationRequest.amount();
        String orderId = memberReservationRequest.orderId();
        String paymentKey = memberReservationRequest.paymentKey();

        PaymentInfo paymentInfo = new PaymentInfo(amount, orderId, paymentKey);
        String base64SecretKey = Base64.getEncoder().encodeToString((secretKey).getBytes());

        return restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", "Basic " + base64SecretKey)
                .body(paymentInfo)
                .retrieve()
                .body(PaymentInfo.class);
    }
}
