package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.CanceledPaymentRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.CanceledReservation;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.request.payment.PaymentRequest;
import roomescape.dto.response.payment.PaymentResponse;
import roomescape.infrastructure.payment.PaymentClient;

@Service
public class TossPaymentService implements PaymentService {
    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final CanceledPaymentRepository canceledPaymentRepository;

    public TossPaymentService(
            PaymentClient paymentClient,
            PaymentRepository paymentRepository,
            CanceledPaymentRepository canceledPaymentRepository
    ) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.canceledPaymentRepository = canceledPaymentRepository;
    }

    @Override
    @Transactional
    public PaymentResponse pay(PaymentRequest paymentRequest, Reservation reservation) {
        Payment payment = paymentRequest.toEntity(reservation);
        paymentRepository.save(payment);
        return paymentClient.pay(paymentRequest);
    }

    @Override
    @Transactional
    public void deletePayment(Reservation reservation, CanceledReservation canceledReservation) {
        paymentRepository.findByReservation(reservation).ifPresent(payment -> {
            canceledPaymentRepository.save(payment.canceled(canceledReservation));
            paymentRepository.delete(payment);
        });
    }
}
