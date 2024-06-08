package roomescape.payment.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentSaveResponse;
import roomescape.payment.dto.TossPaymentCancelResponse;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationCancelReason;

@Service
@Transactional
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(TossPaymentClient tossPaymentClient, PaymentRepository paymentRepository) {
        this.tossPaymentClient = tossPaymentClient;
        this.paymentRepository = paymentRepository;
    }

    public PaymentSaveResponse payForReservation(@Valid PaymentRequest paymentRequest, Reservation reservation) {
        paymentRepository.findByPaymentKey(paymentRequest.paymentKey())
                .ifPresent(payment -> {
                    throw new IllegalArgumentException("결제 완료된 예약 입니다.");
                });
        TossPaymentResponse tossPaymentResponse = tossPaymentClient.requestPayment(paymentRequest);
        Payment savedPayment = paymentRepository.save(tossPaymentResponse.from(reservation));

        return PaymentSaveResponse.toResponse(savedPayment);
    }

    public TossPaymentCancelResponse cancel(Long reservationId, ReservationCancelReason cancelReason) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약의 결제 정보가 없습니다."));
        TossPaymentCancelResponse tossPaymentCancelResponse = tossPaymentClient.requestPaymentCancel(payment.getPaymentKey(), cancelReason);
        payment.cancel();

        return tossPaymentCancelResponse;
    }
}
