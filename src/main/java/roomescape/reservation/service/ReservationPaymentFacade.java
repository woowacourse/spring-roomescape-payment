package roomescape.reservation.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.member.dto.request.LoginMember;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;

@Service
public class ReservationPaymentFacade {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationPaymentFacade(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse createReservationAndSavePayment(
            ReservationRequest request,
            LoginMember loginMember,
            TossPaymentResponse response
    ) {
        ReservationResponse reservation = reservationService.createReservation(request, loginMember.id());
        paymentService.save(response, reservation.id());
        return reservation;
    }
}
