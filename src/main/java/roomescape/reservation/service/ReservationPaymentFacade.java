package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.client.TossPaymentClient;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.member.dto.request.LoginMember;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;

@Service
public class ReservationPaymentFacade {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final TossPaymentClient tossPaymentClient;

    public ReservationPaymentFacade(ReservationService reservationService, PaymentService paymentService, TossPaymentClient tossPaymentClient) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.tossPaymentClient = tossPaymentClient;
    }

    public ReservationResponse createReservationAndSavePayment(
            ReservationRequest request,
            LoginMember loginMember,
            TossPaymentConfirmRequest confirmRequest
    ) {
        ReservationResponse reservation = null;
        try {
            reservation = reservationService.createReservation(request, loginMember.id());
            TossPaymentResponse response = tossPaymentClient.confirmPayment(confirmRequest);
            paymentService.save(response, reservation.id());

            reservationService.confirm(reservation.id());
            return reservation;
        } catch (Exception e) {
            if (reservation != null) {
                reservationService.cancel(reservation.id());
            }
        }
        throw new RuntimeException();
    }
}
