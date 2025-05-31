package roomescape.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.reservation.TossPaymentConfirmRequestDto;

import java.util.Base64;

@Component
public class PaymentClient {

    private static final String PAYMENT_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String PAYMENT_CONFIRM_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC_AUTHORIZATION = "Basic ";

    private final RestClient restClient;

    public PaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public void confirmPayment(TossPaymentConfirmRequestDto requestDto) {
        restClient.post()
                .uri(PAYMENT_CONFIRM_URL)
                .header(AUTHORIZATION, getBasicAuthorizationHeader())
                .body(requestDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();
    }

    private static String getBasicAuthorizationHeader() {
        return BASIC_AUTHORIZATION + Base64.getEncoder().encodeToString(PAYMENT_CONFIRM_SECRET_KEY.getBytes());
    }
}
