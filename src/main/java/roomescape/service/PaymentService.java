package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.exception.PaymentException;
import roomescape.model.Payment;
import roomescape.request.PaymentRequest;
import roomescape.request.ReservationRequest;
import roomescape.response.PaymentResponse;
import roomescape.service.httpclient.TossPaymentClient;

@Service
public class PaymentService {

    private final static long RESERVATION_PRICE = 1999999;

    private final TossPaymentClient tossPaymentClient;

    public PaymentService(final TossPaymentClient tossPaymentClient) {
        this.tossPaymentClient = tossPaymentClient;
    }

    public Payment confirmReservationPayments(ReservationRequest request) {
        validatePayments(request.amount());
        PaymentResponse paymentResponse = tossPaymentClient.confirm(new PaymentRequest(request.paymentKey(), request.orderId(), request.amount()));
        return new Payment(paymentResponse.getPaymentKey(), paymentResponse.getTotalAmount());
    }

    private void validatePayments(long amount) {
        if (RESERVATION_PRICE != amount) {
            throw new PaymentException("클라이언트의 지불 정보가 일치하지 않습니다. 금액 정보 : [%d]".formatted(amount));
        }
    }
}
