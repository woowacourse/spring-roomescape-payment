package roomescape.payment.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.exception.dto.PaymentErrorDto;
import roomescape.exception.custom.PaymentException;
import roomescape.exception.handler.TossPaymentErrorHandler;
import roomescape.payment.application.PaymentClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

import java.time.Duration;

@Service
public class PaymentWithRestClient implements PaymentClient {

    private final RestClient restClient;

    @Value("${security.payment.api.secret-key}")
    private String secretKey;

    public PaymentWithRestClient() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(30L))
                .withConnectTimeout(Duration.ofSeconds(10L));
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);
        this.restClient = RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments")
                .requestFactory(requestFactory)
                .defaultStatusHandler(new TossPaymentErrorHandler())
                .build();
    }

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + secretKey)
                .header("TossPayments-Test-Code", "INVALID_CARD_EXPIRATION")
                .body(paymentRequest)
                .retrieve()
                .onStatus(new TossPaymentErrorHandler())
                .body(PaymentResponse.class);
    }
}
