package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.exception.PaymentException;
import roomescape.model.Payment;
import roomescape.model.Reservation;
import roomescape.repository.PaymentRepository;
import roomescape.request.PaymentRequest;
import roomescape.request.ReservationRequest;
import roomescape.response.PaymentResponse;
import roomescape.service.httpclient.TossPaymentClient;

@Service
public class PaymentService {

    private final static long RESERVATION_PRICE = 1999999;
    private final static String ADMIN_PAYMENT = "ADMIN_PAYMENT";
    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final TossPaymentClient tossPaymentClient, final PaymentRepository paymentRepository) {
        this.tossPaymentClient = tossPaymentClient;
        this.paymentRepository = paymentRepository;
    }

    public void confirmReservationPayments(ReservationRequest request, Reservation reservation) {
        validatePayments(request.amount());
        PaymentResponse paymentResponse = tossPaymentClient.confirm(new PaymentRequest(request.paymentKey(), request.orderId(), request.amount()));
        paymentRepository.save(new Payment(paymentResponse, reservation));
    }

    public void addAdminPayment(final Reservation reservation) {
        paymentRepository.save(new Payment(ADMIN_PAYMENT, 0L, reservation));
    }

    private void validatePayments(long amount) {
        if (RESERVATION_PRICE != amount) {
            throw new PaymentException("클라이언트의 지불 정보가 일치하지 않습니다. 금액 정보 : [%d]".formatted(amount));
        }
    }

}
