package roomescape.system.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.system.exception.error.ErrorType;
import roomescape.system.exception.model.ExceptionResponse;
import roomescape.system.exception.model.InvalidTossErrorCode;
import roomescape.system.exception.model.PaymentException;

public class TossClient implements PaymentClient {
    private final RestClient restClient;

    public TossClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public void confirm(final String authorizations, final ReservationRequest reservationRequest) {

        try {
            restClient.post()
                    .uri("/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(reservationRequest)
                    .header("Authorization", authorizations)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException httpClientErrorException) {
            ExceptionResponse exceptionResponse = httpClientErrorException.getResponseBodyAs(ExceptionResponse.class);
            if (InvalidTossErrorCode.canConvert(exceptionResponse.code())) {
                throw new PaymentException(ErrorType.PAYMENT_ERROR, "잘못된 요청으로 인한 서버 오류",
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            throw new PaymentException(ErrorType.PAYMENT_ERROR, exceptionResponse.message(),
                    httpClientErrorException.getStatusCode().value());
        }
    }

}
