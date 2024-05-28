package roomescape.reservation.facade;

import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.entity.MemberReservation;
import roomescape.reservation.domain.service.ReservationCreateService;
import roomescape.reservation.domain.service.ReservationService;
import roomescape.reservation.domain.service.WaitingReservationService;
import roomescape.reservation.dto.*;

import java.util.List;

@Service
public class ReservationFacadeService {

    private final ReservationService reservationService;
    private final ReservationCreateService reservationCreateService;
    private final WaitingReservationService waitingReservationService;
    private final PaymentService paymentService;

    public ReservationFacadeService(ReservationService reservationService,
                                    ReservationCreateService reservationCreateService,
                                    WaitingReservationService waitingReservationService,
                                    PaymentService paymentService) {
        this.reservationService = reservationService;
        this.reservationCreateService = reservationCreateService;
        this.waitingReservationService = waitingReservationService;
        this.paymentService = paymentService;
    }

    public MemberReservationResponse createReservation(ReservationCreateRequest request) {
        MemberReservation savedMemberReservation = reservationCreateService.createReservation(request);
        return MemberReservationResponse.from(savedMemberReservation);
    }

    public MemberReservationResponse createReservation(MemberReservationCreateRequest request, LoginMember member) {
        ReservationCreateRequest reservationCreateRequest = ReservationCreateRequest.of(request, member);

        MemberReservation savedMemberReservation = reservationCreateService.createReservation(reservationCreateRequest);
        paymentService.confirmPayment(PaymentRequest.from(request), savedMemberReservation);

        return MemberReservationResponse.from(savedMemberReservation);
    }

    public List<MemberReservationResponse> readReservations() {
        return reservationService.readReservations();
    }

    public List<MyReservationResponse> readMemberReservations(LoginMember loginMember) {
        return reservationService.readMemberReservations(loginMember);
    }

    public List<MemberReservationResponse> searchReservations(ReservationSearchRequestParameter searchCondition) {
        return reservationService.searchReservations(searchCondition);
    }

    public List<MemberReservationResponse> readWaitingReservations() {
        return waitingReservationService.readWaitingReservations();
    }

    public void confirmWaitingReservation(Long id) {
        waitingReservationService.confirmWaitingReservation(id);
    }

    public void deleteReservation(Long id) {
        reservationService.deleteReservation(id);
    }

    public void deleteReservation(Long id, LoginMember loginMember) {
        reservationService.deleteReservation(id, loginMember);
    }
}
