package roomescape.payment.application.client;

import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.exception.handler.PaymentApproveExceptionHandler;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;

@Component
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClient {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";
    private static final String COLON = ":";

    private final RestClient restClient;
    private final PaymentApproveExceptionHandler paymentApproveExceptionHandler;
    private final PaymentClientProperties paymentClientProperties;

    public PaymentClient(final RestClient restClient,
                         final PaymentApproveExceptionHandler paymentApproveExceptionHandler,
                         final PaymentClientProperties paymentClientProperties) {
        this.restClient = restClient;
        this.paymentApproveExceptionHandler = paymentApproveExceptionHandler;
        this.paymentClientProperties = paymentClientProperties;
    }

    public PaymentApproveResponse approvePayment(final PaymentApproveRequest paymentApproveRequest) {
        return restClient.post()
                .uri(paymentClientProperties.getConfirmApi())
                .header(AUTHORIZATION, BASIC + toBase64(paymentClientProperties.getSecretKey() + COLON))
                .body(paymentApproveRequest)
                .retrieve()
                .onStatus(paymentApproveExceptionHandler)
                .body(PaymentApproveResponse.class);
    }

    private String toBase64(String rawText) {
        return Base64.getEncoder().encodeToString(rawText.getBytes());
    }
}
