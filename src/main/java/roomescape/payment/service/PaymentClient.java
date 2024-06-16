package roomescape.payment.service;

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
import roomescape.payment.dto.PaymentRefundRequest;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.exception.PaymentUnauthorizedException;
import roomescape.payment.exception.RestClientTimeOutException;

public class PaymentClient {
    private final RestClient restClient;
    private final String authorizationKey;

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
            handleClientException(exception);
        } catch (ResourceAccessException exception) {
            throw new RestClientTimeOutException(exception);
        }
    }

    public void refundPayment(String paymentKey) {
        try {
            restClient.post()
                    .uri("/{paymentKey}/cancel", paymentKey)
                    .header(HttpHeaders.AUTHORIZATION, authorizationKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(PaymentRefundRequest.DEFAULT_REQUEST)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            handleClientException(exception);
        } catch (ResourceAccessException exception) {
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
