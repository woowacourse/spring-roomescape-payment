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
    private static final int CONNECT_TIMEOUT_SECONDS = 1;
    private static final int READ_TIMEOUT_SECONDS = 30;

    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public PaymentClient(PaymentProperties paymentProperties,
                         ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .requestFactory(createPaymentRequestFactory())
                .baseUrl(paymentProperties.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, createPaymentAuthHeader(paymentProperties))
                .build();
    }

    private ClientHttpRequestFactory createPaymentRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));

        return ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory.class, settings);
    }

    private String createPaymentAuthHeader(PaymentProperties paymentProperties) {
        byte[] encodedBytes = Base64.getEncoder().encode((paymentProperties.getSecretKey() + BASIC_DELIMITER).getBytes(StandardCharsets.UTF_8));
        return AUTH_HEADER_PREFIX + new String(encodedBytes);
    }

    public PaymentConfirmOutput confirmPayment(PaymentConfirmInput confirmRequest) {
        try {
            return restClient.method(HttpMethod.POST)
                    .uri("/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
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
