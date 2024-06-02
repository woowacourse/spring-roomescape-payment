package roomescape.reservation.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.dto.WaitingReservationRanking;
import roomescape.reservation.domain.entity.MemberReservation;
import roomescape.reservation.domain.service.ReservationCreateService;
import roomescape.reservation.domain.service.ReservationService;
import roomescape.reservation.domain.service.WaitingReservationService;
import roomescape.reservation.dto.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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

    @Transactional(rollbackFor = Exception.class)
    public MemberReservationResponse createReservation(ReservationCreateRequest request) {
        MemberReservation savedMemberReservation = reservationCreateService.createReservation(request);
        return MemberReservationResponse.from(savedMemberReservation);
    }

    @Transactional(rollbackFor = Exception.class)
    public MemberReservationResponse createReservation(MemberReservationCreateRequest request, LoginMember member) {
        ReservationCreateRequest reservationCreateRequest = ReservationCreateRequest.of(request, member);

        MemberReservation savedMemberReservation = reservationCreateService.createReservation(reservationCreateRequest);
        if (savedMemberReservation.isConfirmed()) {
            paymentService.confirmPayment(PaymentRequest.from(request), savedMemberReservation);
        }

        return MemberReservationResponse.from(savedMemberReservation);
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> readReservations() {
        return reservationService.readReservations().stream()
                .map(MemberReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyReservationResponse> readMemberReservations(LoginMember loginMember) {
        List<MemberReservation> confirmationReservations = reservationService.readConfirmationMemberReservation(loginMember);
        List<WaitingReservationRanking> waitingReservations = reservationService.readWaitingMemberReservation(loginMember);

        return Stream.concat(
                        confirmationReservations.stream()
                                .map(MyReservationResponse::from),
                        waitingReservations.stream()
                                .map(MyReservationResponse::from)
                )
                .sorted(Comparator.comparing(MyReservationResponse::date)
                        .thenComparing(MyReservationResponse::time)
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> searchReservations(ReservationSearchRequestParameter searchCondition) {
        return reservationService.searchReservations(searchCondition).stream()
                .map(MemberReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> readWaitingReservations() {
        return waitingReservationService.readWaitingReservations().stream()
                .map(MemberReservationResponse::from)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmWaitingReservation(Long id) {
        waitingReservationService.confirmWaitingReservation(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteReservation(Long id) {
        MemberReservation memberReservation = reservationService.readReservation(id);
        if (memberReservation.isConfirmed()) {
            paymentService.cancelPayment(memberReservation);
        }
        reservationService.deleteReservation(memberReservation);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteReservation(Long id, LoginMember loginMember) {
        MemberReservation memberReservation = reservationService.readReservation(id);
        if (memberReservation.isConfirmed()) {
            paymentService.cancelPayment(memberReservation);
        }
        reservationService.deleteReservation(memberReservation, loginMember);
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmPendingReservation(Long id, PaymentRequest paymentRequest) {
        MemberReservation memberReservation = reservationService.readReservation(id);
        paymentService.confirmPayment(paymentRequest, memberReservation);
        reservationService.confirmPendingReservation(id);
    }
}
