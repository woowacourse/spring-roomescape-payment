package roomescape.helper.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.service.payment.PaymentStatus;

import java.time.ZonedDateTime;
import java.util.Optional;

@Component
public class PaymentFixture {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment createPayment(Reservation reservation) {
        Payment payment = new Payment("paymentKey", "orderId", 0, "orderName",
                ZonedDateTime.now(), ZonedDateTime.now(), PaymentStatus.DONE, reservation);
        return paymentRepository.save(payment);
    }

    public Optional<Payment> findByReservation(Reservation reservation) {
        return paymentRepository.findByReservation(reservation);
    }
}
