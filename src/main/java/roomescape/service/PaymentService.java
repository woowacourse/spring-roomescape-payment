package roomescape.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.controller.request.PaymentRequest;
import roomescape.controller.request.ReservationRequest;
import roomescape.exception.PaymentException;
import roomescape.service.httpclient.TossPaymentClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class PaymentService {

    private final static long RESERVATION_PRICE = 1999999;

    private final TossPaymentClient tossPaymentClient;

    public PaymentService(final TossPaymentClient tossPaymentClient) {
        this.tossPaymentClient = tossPaymentClient;
    }

    public void confirmReservationPayments(ReservationRequest request) {
        validatePayments(request.amount());
        tossPaymentClient.confirm(new PaymentRequest(request.paymentKey(),request.orderId(),request.amount()));
    }

    private void validatePayments(long amount) {
        if (RESERVATION_PRICE != amount) {
            throw new PaymentException("클라이언트의 지불 정보가 일치하지 않습니다. 금액 정보 : [%d]".formatted(amount));
        }
    }
}
