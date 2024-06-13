package roomescape.infra;

import static roomescape.exception.ExceptionType.EMPTY_RESPONSE_FROM_TOSS_API;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.domain.Payment;
import roomescape.dto.PaymentRequest;
import roomescape.dto.service.TossPaymentResponse;
import roomescape.exception.RoomescapeException;

public class TossPaymentClient implements PaymentClient {

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String DELIMITER = ":";

    private final RestClient restClient;
    private final String authorizationHeaderValue;

    public TossPaymentClient(RestClient restClient, String widgetSecretKey) {
        this.restClient = restClient;
        this.authorizationHeaderValue = "Basic "
                + new String(Base64.getEncoder().encode((widgetSecretKey + DELIMITER).getBytes(StandardCharsets.UTF_8)));

    }

    public Payment requestPaymentApproval(PaymentRequest request) {
        TossPaymentResponse response = restClient.post()
                .header(AUTHORIZATION_HEADER_KEY, authorizationHeaderValue)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(TossPaymentResponse.class);

        validatePaymentResponseNotNull(response);

        return response.toPayment();
    }

    private void validatePaymentResponseNotNull(TossPaymentResponse response) {
        if (response == null) {
            throw new RoomescapeException(EMPTY_RESPONSE_FROM_TOSS_API);
        }
    }

}
