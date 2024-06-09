package roomescape.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentErrorResponse;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.exception.PaymentUnauthorizedException;
import roomescape.payment.exception.RestClientTimeOutException;

public class PaymentClient {
    private final RestClient restClient;
    private final String authorizationKey;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public PaymentClient(RestClient restClient, String authorizationKey) {
        this.restClient = restClient;
        this.authorizationKey = authorizationKey;
    }

    public void confirmPayment(PaymentConfirmRequest request) {
        try {
            restClient.post()
                    .uri("/confirm")
                    .header(HttpHeaders.AUTHORIZATION, authorizationKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            log.error("PaymentService Confirm Response Error, paymentKey = {}, orderId = {}",
                    request.paymentKey(), request.orderId(), exception);
            handleClientException(exception);
        } catch (ResourceAccessException exception) {
            log.error("PaymentService Confirm Timeout Error, paymentKey = {}, orderId = {}",
                    request.paymentKey(), request.orderId(), exception);
            throw new RestClientTimeOutException(exception);
        }
    }

    private void handleClientException(HttpStatusCodeException exception) {
        if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new PaymentUnauthorizedException();
        }
        PaymentErrorResponse errorResponse = exception.getResponseBodyAs(PaymentErrorResponse.class);
        throw new PaymentException(exception.getStatusCode(), errorResponse.message());
    }
}
