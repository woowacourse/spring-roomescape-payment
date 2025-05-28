package roomescape.service;

import java.util.Base64;
import org.springframework.stereotype.Service;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.dto.reservation.ReservationCreateRequest;
import roomescape.dto.reservation.ReservationResponse;

@Service
public class ReservationPaymentService {
    private ReservationService reservationService;
    private PaymentClientService paymentClientService;
    private PaymentService paymentService;

    public ReservationPaymentService(ReservationService reservationService, PaymentClientService paymentClientService,
                                     PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentClientService = paymentClientService;
        this.paymentService = paymentService;
    }

    public ReservationResponse confirmPaymentAndAddReservation(ReservationCreateRequest reservationRequest,
                                                               PaymentConfirmRequest paymentRequest) {
        ReservationResponse reservation = reservationService.createReservation(reservationRequest);
        String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6" + ":";
        String encodedString = "Basic " + Base64.getEncoder().encodeToString(secretKey.getBytes());
        try {
            PaymentConfirmResponse paymentConfirm = paymentClientService.confirm(encodedString, paymentRequest);
            paymentService.createPayment(paymentConfirm, reservation.id());
        } catch (Exception exception) {
            reservationService.deleteReservation(reservation.id());
            throw exception;
        }
        return reservation;
    }
}
