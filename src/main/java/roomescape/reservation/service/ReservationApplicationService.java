package roomescape.reservation.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.domain.AuthInfo;
import roomescape.member.domain.Member;
import roomescape.payment.service.PaymentService;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.reservation.controller.dto.ReservationPaymentRequest;
import roomescape.reservation.controller.dto.ReservationQueryRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.domain.MemberReservation;
import roomescape.reservation.service.dto.MemberReservationCreate;
import roomescape.reservation.service.dto.MyReservationInfo;
import roomescape.reservation.service.dto.WaitingCreate;
import roomescape.reservation.service.services.MemberReservationService;
import roomescape.reservation.service.services.ReservationCommonService;
import roomescape.reservation.service.services.WaitingReservationService;

@Service
@Transactional(readOnly = true)
public class ReservationApplicationService {

    private final ReservationCommonService reservationCommonService;

    private final MemberReservationService memberReservationService;

    private final WaitingReservationService waitingReservationService;
    private final PaymentService paymentService;

    public ReservationApplicationService(ReservationCommonService reservationCommonService,
                                         MemberReservationService memberReservationService,
                                         WaitingReservationService waitingReservationService,
                                         PaymentService paymentService) {
        this.reservationCommonService = reservationCommonService;
        this.memberReservationService = memberReservationService;
        this.waitingReservationService = waitingReservationService;
        this.paymentService = paymentService;
    }

    public List<ReservationResponse> findMemberReservations(ReservationQueryRequest request) {
        return memberReservationService.findMemberReservations(request);
    }

    public List<MyReservationInfo> findMyReservations(AuthInfo authInfo) {
        Member member = reservationCommonService.getMember(authInfo.getId());
        return memberReservationService.findMyReservations(member);
    }

    @Transactional
    public ReservationResponse publish(AuthInfo authInfo, ReservationPaymentRequest reservationPaymentRequest) {
        Member member = reservationCommonService.getMember(authInfo.getId());
        MemberReservation memberReservation = reservationCommonService.getMemberReservation(
                reservationPaymentRequest.memberReservationId());

        reservationCommonService.validateMemberReservation(memberReservation, member);
        reservationCommonService.validatePastReservation(memberReservation.getReservation());

        memberReservation.approve();

        paymentService.pay(
                new PaymentRequest(reservationPaymentRequest.amount(), reservationPaymentRequest.orderId(),
                        reservationPaymentRequest.paymentKey()), memberReservation);

        return ReservationResponse.from(memberReservation);
    }

    @Transactional
    public ReservationResponse createMemberReservation(MemberReservationCreate memberReservationCreate) {
        MemberReservation memberReservation = reservationCommonService.create(
                memberReservationCreate.toReservationCreate());
        paymentService.pay(
                new PaymentRequest(memberReservationCreate.amount(), memberReservationCreate.orderId(),
                        memberReservationCreate.paymentKey()), memberReservation);
        return ReservationResponse.from(memberReservation);
    }

    @Transactional
    public void deleteMemberReservation(AuthInfo authInfo, long memberReservationId) {
        MemberReservation memberReservation = reservationCommonService.getMemberReservation(memberReservationId);
        Member member = reservationCommonService.getMember(authInfo.getId());
        reservationCommonService.delete(member, memberReservation);
        memberReservationService.updateStatus(memberReservation.getReservation());
    }

    @Transactional
    public void delete(long reservationId) {
        memberReservationService.delete(reservationId);
    }

    public List<ReservationResponse> getWaiting() {
        return waitingReservationService.getWaiting();
    }

    @Transactional
    public ReservationResponse addWaiting(WaitingCreate waitingCreate) {
        MemberReservation memberReservation = reservationCommonService.create(waitingCreate.toReservationCreate());
        return ReservationResponse.from(memberReservation);
    }

    @Transactional
    public void deleteWaiting(AuthInfo authInfo, long memberReservationId) {
        MemberReservation memberReservation = reservationCommonService.getMemberReservation(memberReservationId);
        Member member = reservationCommonService.getMember(authInfo.getId());
        waitingReservationService.validateWaitingReservation(memberReservation);

        paymentService.refund(memberReservationId);
        reservationCommonService.delete(member, memberReservation);
    }

    @Transactional
    public void approveWaiting(AuthInfo authInfo, long memberReservationId) {
        Member member = reservationCommonService.getMember(authInfo.getId());
        MemberReservation memberReservation = reservationCommonService.getMemberReservation(memberReservationId);
        waitingReservationService.approveWaiting(member, memberReservation);
    }

    @Transactional
    public void denyWaiting(AuthInfo authInfo, long memberReservationId) {
        Member member = reservationCommonService.getMember(authInfo.getId());
        MemberReservation memberReservation = reservationCommonService.getMemberReservation(memberReservationId);
        waitingReservationService.denyWaiting(member, memberReservation);
    }
}

