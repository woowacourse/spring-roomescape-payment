package roomescape.payment.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentErrorResponse;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.exception.PaymentUnauthorizedException;
import roomescape.payment.exception.RestClientTimeOutException;

public class PaymentService {
    private final RestClient restClient;
    private final String authorizationKey;

    public PaymentService(RestClient restClient, String authorizationKey) {
        this.restClient = restClient;
        this.authorizationKey = authorizationKey;
    }

    public void confirmPayment(PaymentConfirmRequest confirmRequest) {
        try {
            restClient.post()
                    .uri("/confirm")
                    .header(HttpHeaders.AUTHORIZATION, authorizationKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(confirmRequest)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException exception) {
            handleClientException(exception);
        } catch (ResourceAccessException exception) {
            throw new RestClientTimeOutException(exception);
        }
    }

    private void handleClientException(HttpClientErrorException exception) {
        if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new PaymentUnauthorizedException();
        }
        PaymentErrorResponse errorResponse = exception.getResponseBodyAs(PaymentErrorResponse.class);
        throw new PaymentException(exception.getStatusCode(), errorResponse.message());
    }
}
