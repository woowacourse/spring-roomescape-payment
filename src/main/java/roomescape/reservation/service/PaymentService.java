package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.client.PaymentConfirmClient;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.service.dto.request.PaymentConfirmRequest;

@Service
public class PaymentService {

    private final PaymentConfirmClient paymentConfirmClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentConfirmClient paymentConfirmClient, PaymentRepository paymentRepository) {
        this.paymentConfirmClient = paymentConfirmClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment confirmAndSave(PaymentConfirmRequest paymentRequest, Reservation reservation) {
        paymentConfirmClient.confirmPayment(paymentRequest);
        return paymentRepository.save(new Payment(paymentRequest.paymentKey(), paymentRequest.amount()));
    }
}
