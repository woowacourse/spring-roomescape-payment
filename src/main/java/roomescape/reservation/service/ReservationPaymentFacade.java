package roomescape.reservation.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import roomescape.client.TossPaymentClient;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.common.exception.InternalServerException;
import roomescape.member.dto.request.LoginMember;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.ReservationWithPaymentResponse;

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

    public ReservationWithPaymentResponse createReservationAndSavePayment(
            ReservationWithPaymentRequest request,
            LoginMember loginMember
    ) {
        TossPaymentConfirmRequest confirmRequest = new TossPaymentConfirmRequest(
                request.orderId(),
                request.amount(),
                request.paymentKey()
        );
        ReservationWithPaymentResponse reservationWithPendingPayment = reservationService.createReservationWithPendingPayment(request, loginMember.id());

        ResponseEntity<TossPaymentResponse> response = tossPaymentClient.confirmPayment(confirmRequest);

        if (response.getStatusCode().is2xxSuccessful()) {
            paymentService.confirm(reservationWithPendingPayment.paymentId());
            return reservationWithPendingPayment;
        }

        reservationService.deleteReservationById(reservationWithPendingPayment.id());
        paymentService.cancel(reservationWithPendingPayment.paymentId());

        tossPaymentClient.handleTosPamentException(response);
        throw new InternalServerException();
    }
}
