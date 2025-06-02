package roomescape.reservation.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import roomescape.auth.service.dto.LoginMember;
import roomescape.common.exception.InternalServerErrorException;
import roomescape.infrastructure.TossPaymentClient;
import roomescape.infrastructure.dto.request.ConfirmPaymentRequest;
import roomescape.infrastructure.dto.response.ConfirmPaymentResponse;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.ReservationWithPaymentResponse;

@Service
public class CreateReservationWithPaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final CreateReservationService createReservationService;
    private final DeleteReservationService deleteReservationService;
    private final PaymentService paymentService;

    public CreateReservationWithPaymentService(
            TossPaymentClient tossPaymentClient,
            CreateReservationService createReservationService,
            DeleteReservationService deleteReservationService,
            PaymentService paymentService
    ) {
        this.tossPaymentClient = tossPaymentClient;
        this.createReservationService = createReservationService;
        this.deleteReservationService = deleteReservationService;
        this.paymentService = paymentService;
    }

    public ReservationWithPaymentResponse create(ReservationWithPaymentRequest request, LoginMember loginMember) {
        ReservationWithPaymentResponse reservationWithPaymentResponse = createReservationService.createWithPendingPayment(request, loginMember);
        ResponseEntity<ConfirmPaymentResponse> confirmPaymentResponse = tossPaymentClient.postConfirmPayment(ConfirmPaymentRequest.from(request));

        if (confirmPaymentResponse.getStatusCode().is2xxSuccessful()) {
            paymentService.completePayment(reservationWithPaymentResponse.paymentId());
            return reservationWithPaymentResponse;
        }

        deleteReservationService.delete(reservationWithPaymentResponse.id(), loginMember);
        paymentService.failedPayment(reservationWithPaymentResponse.paymentId());

        tossPaymentClient.validateResponse(confirmPaymentResponse);
        throw new InternalServerErrorException();
    }
}
