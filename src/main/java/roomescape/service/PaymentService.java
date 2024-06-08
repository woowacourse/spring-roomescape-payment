package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.service.dto.request.PaymentRequest;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment addPayment(PaymentRequest paymentRequest, Reservation reservation) {
        Payment payment = paymentRequest.toPayment(reservation);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void deletePaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 존재하지 않습니다."));
        paymentRepository.delete(payment);
    }
}
