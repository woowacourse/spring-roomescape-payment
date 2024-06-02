package roomescape.infra;

import static roomescape.exception.ExceptionType.EMPTY_RESPONSE_FROM_TOSS_API;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.domain.Payment;
import roomescape.dto.PaymentRequest;
import roomescape.dto.service.TossPaymentResponse;
import roomescape.exception.PaymentErrorHandler;
import roomescape.exception.RoomescapeException;

public class TossPaymentClient implements PaymentClient {

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String DELIMITER = ":";
    private static final String AUTHORIZATION_HEADER_VALUE = "Basic "
            + new String(Base64.getEncoder().encode((WIDGET_SECRET_KEY + DELIMITER).getBytes(StandardCharsets.UTF_8)));

    private final RestClient restClient;
    private final PaymentErrorHandler paymentErrorHandler;

    public TossPaymentClient(RestClient restClient, PaymentErrorHandler paymentErrorHandler) {
        this.restClient = restClient;
        this.paymentErrorHandler = paymentErrorHandler;
    }

    public Payment requestPaymentApproval(PaymentRequest request) {
        TossPaymentResponse response = restClient.post()
                .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(paymentErrorHandler)
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
