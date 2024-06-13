package roomescape.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.payment.config.TossPaymentSettings;
import roomescape.payment.dto.CancelPaymentRequest;
import roomescape.payment.dto.CreatePaymentRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

import java.util.Base64;

public class TossPaymentClient implements PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient restClient;
    private final ResponseErrorHandler errorHandler;
    private final TossPaymentSettings tossPaymentSettings;

    public TossPaymentClient(final ClientHttpRequestFactory factory,
                             final ResponseErrorHandler errorHandler,
                             final TossPaymentSettings tossPaymentSettings
    ) {
        this.tossPaymentSettings = tossPaymentSettings;
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(tossPaymentSettings.getHostName())
                .build();
        this.errorHandler = errorHandler;
    }

    @Override
    public PaymentConfirmResponse postPayment(final CreatePaymentRequest paymentRequest) {
        final String secret = getSecretKey();
        final PaymentConfirmResponse confirmResponse = restClient.post()
                .uri(tossPaymentSettings.getCreatePaymentApi())
                .header(HttpHeaders.AUTHORIZATION, secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(errorHandler)
                .body(PaymentConfirmResponse.class);
        log.info("토스 결제 응답 = {}", confirmResponse);
        return confirmResponse;
    }

    @Override
    public void cancelPayment(final CancelPaymentRequest paymentRequest) {
        final String secret = getSecretKey();
        restClient.post()
                .uri(tossPaymentSettings.getCancelPaymentApi(), paymentRequest.paymentKey())
                .header(HttpHeaders.AUTHORIZATION, secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(errorHandler);
    }

    private String getSecretKey() {
        return "Basic " + Base64.getEncoder()
                .encodeToString((tossPaymentSettings.getSecretKey() + tossPaymentSettings.getPassword()).getBytes());
    }
}
