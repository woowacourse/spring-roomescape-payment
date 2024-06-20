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
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.Payment;
import roomescape.exception.payment.PaymentCancelErrorCode;
import roomescape.exception.payment.PaymentCancelException;
import roomescape.exception.payment.PaymentConfirmErrorCode;
import roomescape.exception.payment.PaymentConfirmException;
import roomescape.service.payment.config.PaymentExceptionInterceptor;
import roomescape.service.payment.config.PaymentLoggingInterceptor;
import roomescape.service.payment.dto.PaymentCancelInput;
import roomescape.service.payment.dto.PaymentCancelOutput;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;
import roomescape.service.payment.dto.PaymentFailOutput;

import java.io.IOException;
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
    private final PaymentProperties paymentProperties;
    private RestClient restClient;

    public PaymentClient(PaymentProperties paymentProperties,
                         ObjectMapper objectMapper) {
        this.paymentProperties = paymentProperties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .requestFactory(createPaymentRequestFactory())
                .requestInterceptor(new PaymentExceptionInterceptor())
                .requestInterceptor(new PaymentLoggingInterceptor())
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
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getConfirmUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(confirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new PaymentConfirmException(getPaymentConfirmErrorCode(response));
                })
                .body(PaymentConfirmOutput.class);
    }

    /**
     * @see <a href="https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8"> 결제 승인 API 에러 코드 문서</a>
     */
    private PaymentConfirmErrorCode getPaymentConfirmErrorCode(final ClientHttpResponse response) throws IOException {
        PaymentFailOutput confirmFailResponse = objectMapper.readValue(
                response.getBody(), PaymentFailOutput.class);
        return PaymentConfirmErrorCode.findByName(confirmFailResponse.code());
    }

    public PaymentCancelOutput cancelPayment(Payment payment) {
        PaymentCancelInput cancelRequest = new PaymentCancelInput("단순 변심");

        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getCancelUrl(payment.getPaymentKey()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(cancelRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new PaymentCancelException(getPaymentCancelErrorCode(response));
                })
                .body(PaymentCancelOutput.class);
    }

    /**
     * @see <a href="https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%B7%A8%EC%86%8C"> 결제 취소 API 에러 코드 문서</a>
     */
    private PaymentCancelErrorCode getPaymentCancelErrorCode(final ClientHttpResponse response) throws IOException {
        PaymentFailOutput cancelFailResponse = objectMapper.readValue(
                response.getBody(), PaymentFailOutput.class);
        return PaymentCancelErrorCode.findByName(cancelFailResponse.code());
    }
}
