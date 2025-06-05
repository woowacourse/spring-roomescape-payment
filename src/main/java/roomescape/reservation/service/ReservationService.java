package roomescape.reservation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.login.presentation.dto.LoginMemberInfo;
import roomescape.auth.login.presentation.dto.SearchCondition;
import roomescape.common.util.time.DateTime;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.payment.infrastructure.dto.PaymentRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.infrastructure.dto.WaitingWithRank;
import roomescape.member.exception.MemberNotFound;
import roomescape.member.presentation.dto.MemberResponse;
import roomescape.member.presentation.dto.MyReservationResponse;
import roomescape.reservation.domain.*;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.presentation.dto.*;
import roomescape.reservationTime.presentation.dto.ReservationTimeResponse;
import roomescape.theme.presentation.dto.ThemeResponse;

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

    public ReservationResponse createReservation(final ReservationRequest request, final Long memberId) {
        PaymentRequest paymentRequest = new PaymentRequest(request.paymentKey(), request.orderId(), request.amount());
        Reservation reservation = reservationDomainService.saveReservation(request, memberId);
        paymentService.confirmPayment(paymentRequest);
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

    @Transactional
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
