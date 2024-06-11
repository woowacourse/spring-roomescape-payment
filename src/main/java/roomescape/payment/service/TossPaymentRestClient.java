package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import roomescape.exception.BadRequestException;
import roomescape.exception.PaymentFailureException;
import roomescape.payment.PaymentProperties;
import roomescape.payment.dto.PaymentFailure;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

import java.net.URI;
import java.util.Base64;
import java.util.Map;

@Component
public class TossPaymentRestClient {

    private static final String ENDPOINT_CONFIRM = "/v1/payments/confirm";
    private static final String ENDPOINT_CANCEL = "/v1/payments/{paymentKey}/cancel";

    private final RestClient restClient;
    private final PaymentProperties properties;
    private final Logger logger = LoggerFactory.getLogger(TossPaymentRestClient.class);

    public TossPaymentRestClient(ObjectMapper objectMapper, PaymentProperties properties) {
        String authorizationToken = Base64.getEncoder()
                .encodeToString((properties.secret() + ":").getBytes());
        ClientHttpRequestFactory requestFactoryWithTimeout = ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(properties.connectTimeout())
                        .withReadTimeout(properties.readTimeout()));

        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactoryWithTimeout)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + authorizationToken)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, get4xxErrorHandler(objectMapper))
                .defaultStatusHandler(HttpStatusCode::isError, getDefaultErrorHandler(objectMapper))
                .build();
    }

    private RestClient.ResponseSpec.ErrorHandler get4xxErrorHandler(ObjectMapper objectMapper) {
        return (request, response) -> {
            PaymentFailure paymentFailure = objectMapper.readValue(response.getBody(), PaymentFailure.class);
            logger.warn("Request: {} {} / Response: {} {} {}",
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode(),
                    response.getStatusText(),
                    paymentFailure);
            throw new BadRequestException(paymentFailure.message());
        };
    }

    private RestClient.ResponseSpec.ErrorHandler getDefaultErrorHandler(ObjectMapper objectMapper) {
        return (request, response) -> {
            PaymentFailure paymentFailure = objectMapper.readValue(response.getBody(), PaymentFailure.class);
            logger.warn("Request: {} {} / Response: {} {} {}",
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode(),
                    response.getStatusText(),
                    paymentFailure);
            throw new PaymentFailureException(paymentFailure.message());
        };
    }

    private <T> PaymentResponse post(URI uri, T body) {
        return restClient.post()
                .uri(uri)
                .body(body)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(PaymentResponse.class);
    }

    public PaymentResponse confirm(PaymentRequest request) {
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.baseUrl())
                .path(ENDPOINT_CONFIRM)
                .build()
                .toUri();
        return post(uri, request);
    }

    public PaymentResponse cancel(String cancelReason) {
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.baseUrl())
                .path(ENDPOINT_CANCEL)
                .build()
                .toUri();
        return post(uri, Map.of("cancelReason", cancelReason));
    }
}
