package roomescape.infra;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.domain.Payment;
import roomescape.dto.PaymentRequest;
import roomescape.dto.service.TossPaymentResponse;
import roomescape.exception.PaymentErrorHandler;

public class PaymentRestClient {

    // TODO: yaml로 빼기
    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATIONS_HEADER_PREFIX = "Basic ";
    private static final Encoder ENCODER = Base64.getEncoder();
    private static final String BASE64_DELIMITER = ":";

    private final RestClient restClient;
    private final PaymentErrorHandler paymentErrorHandler;

    public PaymentRestClient(RestClient restClient, PaymentErrorHandler paymentErrorHandler) {
        this.restClient = restClient;
        this.paymentErrorHandler = paymentErrorHandler;
    }

    public Payment requestPaymentApproval(PaymentRequest request) {
        TossPaymentResponse response = restClient.post()
                .header(AUTHORIZATION_HEADER_NAME, getAuthorizationsHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(paymentErrorHandler)
                .body(TossPaymentResponse.class);

        return response.toPayment();
    }

    private String getAuthorizationsHeader() {
        byte[] encodedBytes = ENCODER.encode((WIDGET_SECRET_KEY + BASE64_DELIMITER)
                .getBytes(StandardCharsets.UTF_8));

        return AUTHORIZATIONS_HEADER_PREFIX + new String(encodedBytes);
    }
}
