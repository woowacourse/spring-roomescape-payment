package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.entity.Reservation;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void saveReservationPayment(Reservation reservation, PaymentInfo paymentInfo) {
        paymentRepository.save(new Payment(reservation, paymentInfo));
    }
}
