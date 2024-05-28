package roomescape.service.booking.reservation.module;

import java.util.Base64;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.exception.PaymentException;

@Service
public class PaymentService {

    private final String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }


    public PaymentResponse pay(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString(secretKey.getBytes()))
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new PaymentException("[ERROR] 결제에 실패했습니다.");
                })
                .body(PaymentResponse.class);
    }
}
