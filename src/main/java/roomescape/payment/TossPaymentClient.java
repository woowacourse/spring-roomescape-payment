package roomescape.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.payment.dto.PaymentErrorResponse;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.TossPaymentConfirmResponse;

import java.io.IOException;
import java.util.Base64;

public class TossPaymentClient implements PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RestClient restClient;
    private final String secretKey;
    private final String password;
    private final String paymentApi;

    public TossPaymentClient(final HttpComponentsClientHttpRequestFactory factory,
                             @Value("${payments.toss.secret-key}") final String secretKey,
                             @Value("${payments.toss.password}") final String password,
                             @Value("${payments.toss.host-name}") final String hostName,
                             @Value("${payments.toss.payment-api}") final String paymentApi
    ) {
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(hostName)
                .build();
        this.secretKey = secretKey;
        this.password = password;
        this.paymentApi = paymentApi;
    }

    @Override
    public void postPayment(final PaymentRequest paymentRequest) {
        final String secret = "Basic " + Base64.getEncoder().encodeToString((secretKey + password).getBytes());
        final TossPaymentConfirmResponse confirmResponse = restClient.post()
                .uri(paymentApi)
                .header(HttpHeaders.AUTHORIZATION, secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .exchange((request, response) -> getTossPaymentConfirmResponse(response));
        log.info("토스 결제 응답 = {}", confirmResponse);
    }

    private TossPaymentConfirmResponse getTossPaymentConfirmResponse(
            final ConvertibleClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode().isError()) {
            final PaymentErrorResponse tossErrorResponse = objectMapper
                    .readValue(httpResponse.getBody(), PaymentErrorResponse.class);
            throw new TossPaymentException(tossErrorResponse);
        }
        return objectMapper.readValue(httpResponse.getBody(), TossPaymentConfirmResponse.class);
    }
}
