package roomescape.payment.application.client;

import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.application.dto.PaymentGatewayRequest;
import roomescape.payment.application.dto.PaymentGatewayResponse;
import roomescape.payment.application.dto.TossPaymentMapper;
import roomescape.payment.domain.PaymentGateway;
import roomescape.payment.exception.handler.PaymentExceptionHandler;
import roomescape.payment.presentation.dto.request.TossPaymentApproveRequest;
import roomescape.payment.presentation.dto.response.TossPaymentApproveResponse;

@Component
@EnableConfigurationProperties(PaymentClientProperties.class)
public class TossPaymentGateway implements PaymentGateway {

    private static final String BASIC = "Basic ";
    private static final String COLON = ":";
    private static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    private final RestClient restClient;
    private final PaymentExceptionHandler paymentExceptionHandler;
    private final PaymentClientProperties tossPaymentClientProperties;
    private final TossPaymentMapper tossPaymentMapper;
    private final String encodingSecretKey;

    public TossPaymentGateway(final RestClient restClient,
                              final PaymentExceptionHandler paymentExceptionHandler,
                              final PaymentClientProperties tossPaymentClientProperties,
                              final TossPaymentMapper tossPaymentMapper) {
        this.restClient = restClient;
        this.paymentExceptionHandler = paymentExceptionHandler;
        this.tossPaymentClientProperties = tossPaymentClientProperties;
        this.tossPaymentMapper = tossPaymentMapper;
        this.encodingSecretKey = encode(tossPaymentClientProperties.getSecretKey() + COLON);
    }

    @Override
    public PaymentGatewayResponse approvePayment(final PaymentGatewayRequest paymentGatewayRequest) {
        TossPaymentApproveRequest tossRequest = tossPaymentMapper.toTossRequest(paymentGatewayRequest);

        TossPaymentApproveResponse tossResponse = restClient.post()
                .uri(tossPaymentClientProperties.getConfirmApi())
                .header(HttpHeaders.AUTHORIZATION, BASIC + encodingSecretKey)
                .header(IDEMPOTENCY_KEY, paymentGatewayRequest.idempotencyKey())
                .body(tossRequest)
                .retrieve()
                .onStatus(paymentExceptionHandler)
                .body(TossPaymentApproveResponse.class);

        return tossPaymentMapper.toGatewayResponse(tossResponse);
    }

    private String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }
}
