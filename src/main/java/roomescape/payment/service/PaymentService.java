package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.entity.MemberReservation;

import java.util.Optional;

@Service
public class PaymentService {

    private final TossPaymentRestClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository, TossPaymentRestClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public Optional<Payment> findByMemberReservation(MemberReservation memberReservation) {
        return paymentRepository.findByMemberReservation(memberReservation);
    }

    public void confirmPayment(PaymentRequest request, MemberReservation memberReservation) {
        PaymentResponse response = paymentClient.confirm(request);

        Payment payment = new Payment(
                response.paymentKey(),
                response.orderId(),
                response.totalAmount(),
                memberReservation);
        paymentRepository.save(payment);
    }

    public void cancelPayment(MemberReservation memberReservation) {
        String cancelReason = "고객 변심";
        paymentRepository.findByMemberReservation(memberReservation)
                .ifPresent(payment -> {
                    paymentClient.cancel(cancelReason);
                    paymentRepository.delete(payment);
                });
    }
}
