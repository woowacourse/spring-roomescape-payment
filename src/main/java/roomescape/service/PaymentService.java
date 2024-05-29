package roomescape.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.controller.request.PaymentRequest;
import roomescape.controller.request.ReservationRequest;
import roomescape.exception.PaymentException;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class PaymentService {

    private final static long RESERVATION_PRICE = 1999999;

    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void confirmReservationPayments(ReservationRequest request) {
        validatePayments(request.amount());
        restClient.post()
                .uri("/confirm")
                .contentType(APPLICATION_JSON)
                .body(new PaymentRequest(request.paymentKey(), request.orderId(), request.amount()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new PaymentException("결제 정보가 일치하지 않습니다.");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new PaymentException("결제 시스템이 원활하게 동작하지 않습니다.");
                }).toBodilessEntity();
    }

    private void validatePayments(long amount) {
        if (RESERVATION_PRICE != amount) {
            throw new PaymentException("클라이언트의 지불 정보가 일치하지 않습니다. 금액 정보 : [%d]".formatted(amount));
        }
    }
}
