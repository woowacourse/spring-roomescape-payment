package roomescape.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.payment.PaymentConfirmErrorCode;
import roomescape.exception.payment.PaymentConfirmException;
import roomescape.exception.payment.PaymentIOException;
import roomescape.exception.payment.PaymentTimeoutException;
import roomescape.service.payment.dto.PaymentConfirmFailOutput;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Component
public class PaymentClient {
    private static final String BASIC_DELIMITER = ":";
    private static final String AUTH_HEADER_PREFIX = "Basic ";
    private final PaymentProperties paymentProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public PaymentClient(PaymentProperties paymentProperties,
                         RestClient.Builder restClientBuilder,
                         ObjectMapper objectMapper) {
        this.paymentProperties = paymentProperties;
        this.objectMapper = objectMapper;

        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(1))
                .withReadTimeout(Duration.ofSeconds(30));
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory.class, settings);
        this.restClient = restClientBuilder.requestFactory(requestFactory).build();
    }

    public PaymentConfirmOutput confirmPayment(PaymentConfirmInput confirmRequest) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((paymentProperties.getSecretKey() + BASIC_DELIMITER).getBytes(StandardCharsets.UTF_8));
        String authorizations = AUTH_HEADER_PREFIX + new String(encodedBytes);

        try {
            return restClient.method(HttpMethod.POST)
                    .uri(paymentProperties.getUrl() + "/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, authorizations)
                    .body(confirmRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new PaymentConfirmException(getPaymentConfirmErrorCode(response));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new PaymentConfirmException(getPaymentConfirmErrorCode(response));
                    })
                    .body(PaymentConfirmOutput.class);

        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new PaymentTimeoutException(e);
            }
            throw new PaymentIOException(e);
        } catch (Exception e) {
            throw new PaymentConfirmException(e);
        }
    }

    private PaymentConfirmErrorCode getPaymentConfirmErrorCode(final ClientHttpResponse response) throws IOException {
        PaymentConfirmFailOutput confirmFailResponse = objectMapper.readValue(
                response.getBody(), PaymentConfirmFailOutput.class);
        return PaymentConfirmErrorCode.findByName(confirmFailResponse.code());
    }
}
