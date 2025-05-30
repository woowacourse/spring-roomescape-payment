package roomescape.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.dto.reservation.ReservationCreateRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.util.AuthorizationHeaderProvider;

@Service
public class ReservationPaymentService {
    @Value("toss.secret-key")
    private String secretKey;

    private final ReservationService reservationService;
    private final PaymentClientService paymentClientService;
    private final PaymentService paymentService;
    private final AuthorizationHeaderProvider authorizationHeaderProvider;

    public ReservationPaymentService(ReservationService reservationService, PaymentClientService paymentClientService,
                                     PaymentService paymentService,
                                     AuthorizationHeaderProvider authorizationHeaderProvider) {
        this.reservationService = reservationService;
        this.paymentClientService = paymentClientService;
        this.paymentService = paymentService;
        this.authorizationHeaderProvider = authorizationHeaderProvider;
    }

    @Transactional
    public ReservationResponse confirmPaymentAndAddReservation(ReservationCreateRequest reservationRequest,
                                                               PaymentConfirmRequest paymentRequest) {
        ReservationResponse reservation = reservationService.createReservation(reservationRequest);
        String providedAuthorization = authorizationHeaderProvider.provide(secretKey);
        PaymentConfirmResponse paymentConfirm = paymentClientService.confirm(providedAuthorization, paymentRequest);
        paymentService.createPayment(paymentConfirm, reservation.id());
        return reservation;
    }
}
