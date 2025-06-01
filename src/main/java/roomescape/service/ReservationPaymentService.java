package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.dto.reservation.ReservationCreateRequest;
import roomescape.dto.reservation.ReservationResponse;

@Service
public class ReservationPaymentService {
    private final ReservationService reservationService;
    private final PaymentClientService paymentClientService;
    private final PaymentService paymentService;

    public ReservationPaymentService(
            ReservationService reservationService,
            PaymentClientService paymentClientService,
            PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentClientService = paymentClientService;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse confirmPaymentAndAddReservation(ReservationCreateRequest reservationRequest,
                                                               PaymentConfirmRequest paymentRequest) {
        ReservationResponse reservation = reservationService.createReservation(reservationRequest);
        PaymentConfirmResponse paymentConfirm = paymentClientService.confirm(paymentRequest);
        paymentService.createPayment(paymentConfirm, reservation.id());
        return reservation;
    }
}