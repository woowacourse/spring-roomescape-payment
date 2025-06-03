package roomescape.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.config.dto.RestClientErrorResponse;
import roomescape.dto.reservation.TossPaymentConfirmRequestDto;

import java.util.Base64;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

@Component
public class PaymentClient {

    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1";
    private static final String PAYMENT_CONFIRM_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    public static final String PAYMENT_AUTHORIZATION_HEADER =
            "Basic " + Base64.getEncoder().encodeToString(PAYMENT_CONFIRM_SECRET_KEY.getBytes());

    private final ObjectMapper objectMapper;

    private final RestClient restClient;

    public PaymentClient(ObjectMapper objectMapper, final RestClient restClient) {
        this.objectMapper = objectMapper;
        this.restClient = restClient.mutate()
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    RestClientErrorResponse restClientErrorResponse = objectMapper.readValue(res.getBody(), RestClientErrorResponse.class);
                    if (restClientErrorResponse.isInvisibleError()) {
                        throw new PaymentConfirmServerException();
                    }
                    throw new PaymentConfirmClientException(restClientErrorResponse.getMessage());
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new PaymentConfirmServerException();
                })
                .build();
    }

    public void confirmPayment(TossPaymentConfirmRequestDto requestDto) {
        restClient.post()
                .uri(TOSS_API_URL + "/payments/confirm")
                .header("Authorization", PAYMENT_AUTHORIZATION_HEADER)
                .body(requestDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();
    }
}
