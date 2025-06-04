package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.request.AddReservationRequest;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.request.ConfirmWaitReservationRequest;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.request.CreateWaitReservationRequest;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.response.MyReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWaitResponse;
import roomescape.entity.Reservation;

@Service
@Transactional
public class ReservationFacadeService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationFacadeService(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public ReservationResponse addReservation(CreateReservationRequest request,
                                              LoginMemberRequest loginMemberRequest) {
        AddReservationRequest addReservationRequest = AddReservationRequest.from(request);
        Reservation reservation = reservationService.addReservation(addReservationRequest, loginMemberRequest);

        ConfirmPaymentRequest confirmPaymentRequest = ConfirmPaymentRequest.from(request);
        paymentService.confirmPayment(confirmPaymentRequest, reservation);

        return ReservationResponse.from(reservation);
    }

    public ReservationResponse pendingToReserve(Long reservationId,
                                                ConfirmWaitReservationRequest request,
                                                LoginMemberRequest loginMemberRequest) {

        Reservation reservation = reservationService.pendingToReserve(reservationId, loginMemberRequest);

        ConfirmPaymentRequest confirmPaymentRequest = ConfirmPaymentRequest.from(request);
        paymentService.confirmPayment(confirmPaymentRequest, reservation);

        return ReservationResponse.from(reservation);
    }

    public List<ReservationResponse> findAllReservation() {
        return reservationService.findAllReserved();
    }

    public List<MyReservationResponse> findAllReservationOfMember(LoginMemberRequest loginMemberRequest) {
        return reservationService.findAllReservationOfMember(loginMemberRequest.id());
    }

    public ReservationWaitResponse addWaitReservation(CreateWaitReservationRequest request,
                                                      LoginMemberRequest loginMemberRequest) {
        return reservationService.addWaitReservation(request, loginMemberRequest);
    }

    public void deleteReservation(final Long id) {
        reservationService.deleteReservation(id);
        paymentService.refundReservation(id);
    }
}
