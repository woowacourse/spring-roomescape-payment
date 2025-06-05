package roomescape.payment.application.client;

import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.exception.PaymentClientException;
import roomescape.payment.exception.PaymentForbiddenException;
import roomescape.payment.exception.handler.PaymentExceptionHandler;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.TossPaymentApproveResponse;

@Component
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClient {

    private static final String BASIC = "Basic ";
    private static final String COLON = ":";

    private final RestClient restClient;
    private final PaymentExceptionHandler paymentExceptionHandler;
    private final PaymentClientProperties paymentClientProperties;
    private final String encodingSecretKey;

    public PaymentClient(final RestClient restClient,
                         final PaymentExceptionHandler paymentExceptionHandler,
                         final PaymentClientProperties paymentClientProperties) {
        this.restClient = restClient;
        this.paymentExceptionHandler = paymentExceptionHandler;
        this.paymentClientProperties = paymentClientProperties;
        this.encodingSecretKey = encode(paymentClientProperties.getSecretKey() + COLON);
    }

    @Retryable(retryFor = {PaymentClientException.class, PaymentForbiddenException.class})
    public TossPaymentApproveResponse approvePayment(final PaymentApproveRequest paymentApproveRequest) {
        return restClient.post()
                .uri(paymentClientProperties.getConfirmApi())
                .header(HttpHeaders.AUTHORIZATION, BASIC + encodingSecretKey)
                .body(paymentApproveRequest)
                .retrieve()
                .onStatus(paymentExceptionHandler)
                .body(TossPaymentApproveResponse.class);
    }

    private String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }
}
