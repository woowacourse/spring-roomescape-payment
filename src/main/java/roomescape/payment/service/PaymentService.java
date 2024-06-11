package roomescape.payment.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import roomescape.payment.client.TossPayRestClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.domain.PaymentWAL;
import roomescape.payment.domain.PaymentWALStatus;
import roomescape.payment.dto.CancelRequest;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.repository.PaymentWALRepository;
import roomescape.reservation.dto.ReservationResponse;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentWALRepository paymentWALRepository;
    private final TossPayRestClient tossPayRestClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentWALRepository paymentWALRepository,
                          TossPayRestClient tossPayRestClient) {
        this.paymentRepository = paymentRepository;
        this.paymentWALRepository = paymentWALRepository;
        this.tossPayRestClient = tossPayRestClient;
    }

    public Payment createPayment(PaymentRequest paymentRequest, Long reservationId) {
        PaymentWAL paymentWAL = new PaymentWAL(PaymentWALStatus.READY, paymentRequest.paymentKey());
        paymentWALRepository.save(paymentWAL);
        Payment readyPayment = paymentRequest.toPaymentStatusReady();
        readyPayment.bindToReservation(reservationId);

        return paymentRepository.save(readyPayment);
    }

    public PaymentResponse confirm(Payment payment) {
        PaymentWAL paymentWAL = paymentWALRepository.findByPaymentKey(payment.getPaymentKey())
                .orElseThrow(() -> new IllegalArgumentException("진행중인 결제가 아닙니다."));
        paymentWAL.updateStatus(PaymentWALStatus.PAY_REQUEST);

        Payment foundPayment = paymentRepository.findByReservationId(payment.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하는 결제 내역이 없습니다."));
        try {
            Payment confirmedPayment = tossPayRestClient.pay(
                    new PaymentRequest(foundPayment.getOrderId(), foundPayment.getTotalAmount().intValue(),
                            foundPayment.getPaymentKey()));
            foundPayment.updatePaymentStatus(confirmedPayment.getStatus());
            paymentWAL.updateStatus(PaymentWALStatus.PAY_CONFIRMED);
        } catch (Exception exception) {
            foundPayment.updatePaymentStatus(PaymentStatus.REJECTED);
            paymentWAL.setErrorMessage(exception.getMessage());
            paymentWAL.updateStatus(PaymentWALStatus.PAY_REJECTED);
            throw exception;
        }
        return PaymentResponse.toResponse(foundPayment);
    }

    public void cancel(ReservationResponse canceledReservation) {
        Optional<Payment> paymentById = paymentRepository.findByReservationId(canceledReservation.id());
        paymentById.ifPresent(
                payment -> {
                    tossPayRestClient.cancel(new CancelRequest(payment.getPaymentKey()));
                    payment.cancel();
                    paymentRepository.save(payment);
                }
        );
    }
}
