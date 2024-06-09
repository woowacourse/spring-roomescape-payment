package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.service.dto.request.PaymentCreateRequest;

import java.util.NoSuchElementException;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment addPayment(PaymentCreateRequest request) {
        Payment payment = request.toPayment();
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment deleteByReservation(Reservation reservation) {
        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new NoSuchElementException("결제 정보가 존재하지 않습니다."));
        paymentRepository.delete(payment);
        return payment;
    }

    @Transactional
    public void rollbackDelete(Payment payment) {
        Payment newPayment = payment.copy();
        paymentRepository.save(newPayment);
    }
}
