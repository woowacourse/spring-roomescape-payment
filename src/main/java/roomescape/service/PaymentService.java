package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.domain.PaymentClient;
import roomescape.dto.PaymentRequest;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;
    private final ReservationService reservationService;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, ReservationService reservationService,
                          PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.reservationService = reservationService;
        this.paymentRepository = paymentRepository;
    }

    public Payment pay(long reservationId, PaymentRequest paymentRequest) {
        Payment payment = paymentClient.pay(paymentRequest);
        reservationService.pay(reservationId, payment);
        return paymentRepository.save(payment);
    }
}
