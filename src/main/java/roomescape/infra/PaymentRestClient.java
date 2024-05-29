package roomescape.infra;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.domain.Payment;
import roomescape.dto.PaymentRequest;
import roomescape.dto.service.TossPaymentResponse;
import roomescape.exception.PaymentClientErrorHandler;
import roomescape.exception.PaymentServerErrorHandler;

public class PaymentRestClient {

    private final RestClient restClient;
    private final PaymentClientErrorHandler paymentClientErrorHandler;
    private final PaymentServerErrorHandler paymentServerErrorHandler;

    public PaymentRestClient(
            RestClient restClient,
            PaymentClientErrorHandler paymentClientErrorHandler,
            PaymentServerErrorHandler paymentServerErrorHandler
    ) {
        this.restClient = restClient;
        this.paymentClientErrorHandler = paymentClientErrorHandler;
        this.paymentServerErrorHandler = paymentServerErrorHandler;
    }

    public Payment requestPaymentApproval(PaymentRequest request) {
        // TODO: yaml로 빼기
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        TossPaymentResponse response = restClient.post()
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(paymentClientErrorHandler)
                .onStatus(paymentServerErrorHandler)
                .body(TossPaymentResponse.class);

        return response.toPayment();
    }
}
