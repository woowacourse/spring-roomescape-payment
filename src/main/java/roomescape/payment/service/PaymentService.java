package roomescape.payment.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.exception.PaymentNotFoundException;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.PaymentRequest;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    @Transactional
    public void updatePaymentWithConfirm(final PaymentResponse response, final Reservation reservation) {
        Payment payment = paymentRepository.findByPaymentKey(response.paymentKey())
                .orElseThrow(() -> new PaymentNotFoundException("요청한 paymentKey에 해당하는 결제가 없습니다."));
        payment.confirm(response.status(), response.requestedAt());
    }

    @Transactional
    public void createPaymentWithRequest(final Reservation reservation, final PaymentRequest request) {
        Payment payment = new Payment(reservation, request.paymentKey(), request.orderId(), request.paymentType(),
                request.amount());
        paymentRepository.save(payment);
    }

    public Payment findByReservationIdOrNull(final Long id) {
        return paymentRepository.findByReservationId(id)
                .orElse(null);

    }
}
