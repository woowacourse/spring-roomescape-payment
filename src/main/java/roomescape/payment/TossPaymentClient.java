package roomescape.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.TossPaymentConfirmResponse;

import java.util.Base64;

public class TossPaymentClient implements PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient restClient;
    private final ResponseErrorHandler errorHandler;
    private final String secretKey;
    private final String password;
    private final String paymentApi;

    public TossPaymentClient(final ClientHttpRequestFactory factory,
                             final ResponseErrorHandler errorHandler,
                             @Value("${payments.toss.secret-key}") final String secretKey,
                             @Value("${payments.toss.password}") final String password,
                             @Value("${payments.toss.host-name}") final String hostName,
                             @Value("${payments.toss.payment-api}") final String paymentApi
    ) {
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(hostName)
                .build();
        this.errorHandler = errorHandler;
        this.secretKey = secretKey;
        this.password = password;
        this.paymentApi = paymentApi;
    }

    @Override
    public TossPaymentConfirmResponse postPayment(final PaymentRequest paymentRequest) {
        final String secret = "Basic " + Base64.getEncoder().encodeToString((secretKey + password).getBytes());
        final TossPaymentConfirmResponse confirmResponse = restClient.post()
                .uri(paymentApi)
                .header(HttpHeaders.AUTHORIZATION, secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(errorHandler)
                .body(TossPaymentConfirmResponse.class);
        log.info("토스 결제 응답 = {}", confirmResponse);
        return confirmResponse;
    }
}
