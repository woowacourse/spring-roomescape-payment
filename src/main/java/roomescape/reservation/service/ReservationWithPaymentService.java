package roomescape.reservation.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.dto.response.ReservationPaymentResponse;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;

@Service
@Transactional
public class ReservationWithPaymentService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationWithPaymentService(ReservationService reservationService,
                                         PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public ReservationResponse addReservationWithPayment(ReservationRequest request, PaymentResponse paymentInfo,
                                                         Long memberId) {
        Reservation reservation = reservationService.addReservation(request, memberId);
        ReservationPaymentResponse reservationPaymentResponse = paymentService.savePayment(paymentInfo, reservation);

        return reservationPaymentResponse.reservation();
    }

    public void cancelPaymentWhenErrorOccurred(PaymentCancelResponse cancelInfo, String paymentKey) {
        paymentService.cancelPaymentWhenErrorOccurred(cancelInfo, paymentKey);
    }

    public PaymentCancelRequest removeReservationWithPayment(Long reservationId, Long memberId) {
        PaymentCancelRequest paymentCancelRequest = paymentService.cancelPaymentByAdmin(reservationId);
        reservationService.removeReservationById(reservationId, memberId);
        return paymentCancelRequest;
    }

    @Transactional(readOnly = true)
    public boolean isNotPaidReservation(Long reservationId) {
        return paymentService.findPaymentByReservationId(reservationId).isEmpty();
    }

    public void updateCanceledTime(String paymentKey, LocalDateTime canceledAt) {
        paymentService.updateCanceledTime(paymentKey, canceledAt);
    }
}
