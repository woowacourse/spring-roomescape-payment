package roomescape.infra;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentRequest;
import roomescape.dto.service.PaymentApprovalResult;
import roomescape.exception.PaymentErrorHandler;

@Component
public class PaymentRestClient {

    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
    private static final Encoder ENCODER = Base64.getEncoder();
    private static final String BASE64_DELIMITER = ":";

    private final String widgetSecretKey;
    private final RestClient restClient;
    private final PaymentErrorHandler paymentErrorHandler;

    public PaymentRestClient(
            @Value("${secret.key.widget}") String widgetSecretKey,
            RestClient restClient,
            PaymentErrorHandler paymentErrorHandler
    ) {
        this.widgetSecretKey = widgetSecretKey;
        this.restClient = restClient;
        this.paymentErrorHandler = paymentErrorHandler;
    }

    public PaymentApprovalResult requestPaymentApproval(PaymentRequest request) {
        return restClient.post()
                .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(paymentErrorHandler)
                .body(PaymentApprovalResult.class);
    }

    private String getAuthorizationHeader() {
        byte[] encodedBytes = ENCODER.encode((widgetSecretKey + BASE64_DELIMITER)
                .getBytes(StandardCharsets.UTF_8));

        return AUTHORIZATION_HEADER_PREFIX + new String(encodedBytes);
    }
}
