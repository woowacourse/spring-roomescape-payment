package roomescape.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.client.PaymentClient;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.exception.RoomescapeException;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public void confirmPayment(PaymentRequest request, Reservation reservation) {
        PaymentResponse response = paymentClient.requestPayment(request);
        Payment payment = new Payment(reservation, response.paymentKey(), response.totalAmount());
        paymentRepository.save(payment);
    }

    public void refundPayment(long reservationId) {
        Payment payment = paymentRepository.findFirstByReservationId(reservationId)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.BAD_REQUEST, "결제 내역이 존재하지 않습니다."));
        payment.refund();
    }
}
