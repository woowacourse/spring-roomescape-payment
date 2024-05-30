package roomescape.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import roomescape.controller.dto.PaymentErrorMessageResponse;
import roomescape.global.exception.RoomescapeException;
import roomescape.service.conponent.PaymentWithRestClient;
import roomescape.service.dto.PaymentRequestDto;

@Service
public class PaymentService {

    public static final String AUTHORIZATION = "Authorization";

    private final PaymentWithRestClient paymentWithRestClient;

    public PaymentService(PaymentWithRestClient paymentWithRestClient) {
        this.paymentWithRestClient = paymentWithRestClient;
    }

    public void pay(String orderId, long amount, String paymentKey) {
        RestClient restClient = paymentWithRestClient.getRestClient();

        try {
            restClient.post()
                    .uri(paymentWithRestClient.getPaymentServerURL())
                    .body(new PaymentRequestDto(orderId, amount, paymentKey))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, paymentWithRestClient.getAuthorizations())
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            PaymentErrorMessageResponse response = e.getResponseBodyAs(PaymentErrorMessageResponse.class);
            throw new RoomescapeException(response.message());
        }
    }
}
