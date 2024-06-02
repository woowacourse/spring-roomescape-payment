package roomescape.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentErrorResponse;
import roomescape.payment.exception.PaymentException;

@Service
public class TossPaymentService implements PaymentService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RestClient restClient;

    public TossPaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void confirmPayment(PaymentConfirmRequest confirmRequest) {
        try {
            restClient.post()
                    .uri("/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(confirmRequest)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            handleClientException(confirmRequest, exception);
        } catch (ResourceAccessException exception) {
            log.error(exception.getMessage(), exception);
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 요청 시간이 만료되었습니다.");
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제를 실패 했습니다.");
        }
    }

    private void handleClientException(PaymentConfirmRequest confirmRequest, HttpStatusCodeException exception) {
        if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            log.error("토스 결제 인증 에러: paymentKey = {}", confirmRequest.paymentKey(), exception);
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "토스 결제 인증 에러입니다.");
        }
        log.error("토스 결제 에러 message: paymentKey = {}, message = {}, body = {}",
                confirmRequest.paymentKey(), exception.getMessage(), exception.getResponseBodyAsString());

        PaymentErrorResponse errorResponse = exception.getResponseBodyAs(PaymentErrorResponse.class);
        if (errorResponse == null) {
            throw new PaymentException(exception.getStatusCode(), exception.getMessage());
        }
        throw new PaymentException(exception.getStatusCode(), errorResponse.message());
    }

}
