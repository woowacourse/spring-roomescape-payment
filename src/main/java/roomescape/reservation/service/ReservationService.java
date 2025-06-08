package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.login.presentation.dto.LoginMemberInfo;
import roomescape.auth.login.presentation.dto.SearchCondition;
import roomescape.member.presentation.dto.MyReservationResponse;
import roomescape.payment.infrastructure.dto.PaymentRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservation.presentation.dto.ReservationResponse;
import roomescape.reservation.presentation.dto.WaitingResponse;

import java.util.List;

@Service
public class ReservationService {

    private final WaitingDomainService waitingDomainService;
    private final PaymentService paymentService;
    private final ReservationDomainService reservationDomainService;

    public ReservationService(
        final WaitingDomainService waitingDomainService,
        final PaymentService paymentService,
        ReservationDomainService reservationDomainService) {
        this.waitingDomainService = waitingDomainService;
        this.paymentService = paymentService;
        this.reservationDomainService = reservationDomainService;
    }

    @Transactional
    public ReservationResponse createReservation(final ReservationRequest request, final Long memberId) {
        PaymentRequest paymentRequest = new PaymentRequest(request.paymentKey(), request.orderId(), request.amount());
        paymentService.confirmPayment(paymentRequest);
        Reservation reservation = reservationDomainService.saveReservation(request, memberId);
        paymentService.savePayment(reservation, paymentRequest);
        return ReservationResponse.from(reservation);
    }

    public WaitingResponse createWaiting(final ReservationRequest request, final Long memberId) {
        return waitingDomainService.createWaiting(request, memberId);
    }

    public List<ReservationResponse> getReservations() {
        return reservationDomainService.getReservations();
    }

    public void deleteReservationById(final Long id) {
        reservationDomainService.deleteReservationById(id);
    }

    public void deleteWaiting(final Long waitingId) {
        waitingDomainService.deleteWaiting(waitingId);
    }

    public List<ReservationResponse> searchReservationWithCondition(final SearchCondition condition) {
        return reservationDomainService.searchReservationWithCondition(condition);
    }

    public List<MyReservationResponse> getMemberReservations(final LoginMemberInfo loginMemberInfo) {
        return reservationDomainService.getMemberReservations(loginMemberInfo);
    }

    public List<ReservationResponse> findAllWaitings() {
        return waitingDomainService.findAllWaitings();
    }
}
