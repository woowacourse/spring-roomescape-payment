package roomescape.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.controller.request.PaymentRequest;
import roomescape.controller.request.ReservationRequest;
import roomescape.exception.InvalidPaymentInformationException;
import roomescape.exception.PaymentException;
import roomescape.exception.PaymentServerErrorException;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class PaymentService {

    private final static long RESERVATION_PRICE = 1999999;
    private final RestClient restClient;
    @Value("${payment.toss.payment-confirm-url}")
    private String confirmUrl;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void confirmReservationPayments(ReservationRequest request) {
        validatePayments(request.amount());
        restClient.post()
                .uri(confirmUrl)
                .contentType(APPLICATION_JSON)
                .body(new PaymentRequest(request.paymentKey(), request.orderId(), request.amount()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new InvalidPaymentInformationException();
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new PaymentServerErrorException();
                }).toBodilessEntity();
    }

    private void validatePayments(long amount) {
        if (RESERVATION_PRICE != amount) {
            throw new PaymentException("클라이언트의 지불 정보가 일치하지 않습니다. 금액 정보 : [%d]".formatted(amount));
        }
    }
}
