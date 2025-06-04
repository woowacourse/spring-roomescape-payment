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
        try {
            tossPaymentClient.postConfirmPayment(
                    ConfirmPaymentRequest.from(request),
                    UUID.randomUUID()
            );
            paymentService.completePayment(reservationWithPaymentResponse.paymentId());
            return reservationWithPaymentResponse;
        } catch (Exception e) {
            deleteReservationService.delete(reservationWithPaymentResponse.id(), loginMember);
            paymentService.failedPayment(reservationWithPaymentResponse.paymentId());
            throw e;
        }
    }
}
