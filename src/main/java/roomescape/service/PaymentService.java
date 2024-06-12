package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.domain.PaymentClient;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
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
        PaymentResponse paymentResponse = paymentClient.pay(paymentRequest);
        Payment payed = paymentRepository.save(new Payment(paymentResponse.paymentKey(),
                paymentResponse.orderId(),
                paymentResponse.totalAmount()));
        reservationService.pay(reservationId, payed);
        return payed;
    }
}
