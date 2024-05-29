package roomescape.service.booking.reservation.module;

import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.dto.payment.PaymentErrorResponse;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.exception.PaymentException;

@Service
public class PaymentService {

    private final String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6바보:";
    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }


    public PaymentResponse pay(PaymentRequest paymentRequest) {
        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + Base64.getEncoder()
                            .encodeToString(secretKey.getBytes()))
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResponse errorResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            throw new PaymentException(errorResponse.message(), e.getStatusCode());
        }
    }
}
