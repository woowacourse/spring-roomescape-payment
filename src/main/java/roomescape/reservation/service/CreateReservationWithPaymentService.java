package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.auth.service.dto.LoginMember;
import roomescape.payment.infrastructure.TossPaymentClient;
import roomescape.payment.infrastructure.dto.request.ConfirmPaymentRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.ReservationWithPaymentResponse;

import java.util.UUID;

@Service
public class CreateReservationWithPaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final ReservationCommandService reservationCommandService;
    private final PaymentService paymentService;

    public CreateReservationWithPaymentService(
            final TossPaymentClient tossPaymentClient,
            final ReservationCommandService reservationCommandService,
            final PaymentService paymentService
    ) {
        this.tossPaymentClient = tossPaymentClient;
        this.reservationCommandService = reservationCommandService;
        this.paymentService = paymentService;
    }

    public ReservationWithPaymentResponse create(
            final ReservationWithPaymentRequest request,
            final LoginMember loginMember
    ) {
        ReservationWithPaymentResponse reservationWithPaymentResponse = reservationCommandService.createWithPendingPayment(request, loginMember);
        try {
            tossPaymentClient.postConfirmPayment(
                    ConfirmPaymentRequest.from(request),
                    UUID.randomUUID()
            );
            paymentService.completePayment(reservationWithPaymentResponse.paymentId());
            reservationCommandService.confirm(reservationWithPaymentResponse.id());
            return reservationWithPaymentResponse;
        } catch (Exception e) {
            reservationCommandService.cancel(reservationWithPaymentResponse.id(), loginMember);
            paymentService.failedPayment(reservationWithPaymentResponse.paymentId());
            throw e;
        }
    }
}
