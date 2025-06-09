package roomescape.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.PaymentClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;
import roomescape.repository.PaymentRepository;

@Service
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public ConfirmPaymentResponse processPayment(ConfirmPaymentRequest paymentRequest, Reservation reservation) {
        Payment payment = new Payment(paymentRequest.orderId(),
                paymentRequest.paymentKey(),
                paymentRequest.amount(),
                reservation);
        reservation.payForReservation(payment);
        paymentRepository.save(payment);
        return paymentClient.confirmPayment(paymentRequest);
    }

    public void refundReservation(Long reservationId) {
        paymentRepository.findFetchByReservationId(reservationId)
                .ifPresentOrElse(paymentClient::refund,
                        () -> log.warn("Attempting to refund with a non-existent payment by reservationId: {}",
                                reservationId));
    }
}
