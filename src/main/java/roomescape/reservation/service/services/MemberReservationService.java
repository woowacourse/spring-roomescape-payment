package roomescape.reservation.service.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.reservation.controller.dto.ReservationQueryRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.controller.dto.WaitingResponse;
import roomescape.reservation.domain.MemberReservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.repository.MemberReservationRepository;
import roomescape.reservation.service.dto.MyReservationInfo;
import roomescape.reservation.service.dto.PaymentInfo;

@Service
@Transactional(readOnly = true)
public class MemberReservationService {


    private final MemberReservationRepository memberReservationRepository;

    private final PaymentRepository paymentRepository;

    public MemberReservationService(MemberReservationRepository memberReservationRepository, final PaymentRepository paymentRepository) {
        this.memberReservationRepository = memberReservationRepository;
        this.paymentRepository = paymentRepository;
    }


    public List<ReservationResponse> findMemberReservations(ReservationQueryRequest request) {
        return memberReservationRepository.findBy(
                        request.getMemberId(),
                        request.getThemeId(),
                        ReservationStatus.APPROVED,
                        request.getStartDate(),
                        request.getEndDate())
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<MyReservationInfo> findMyReservations(Member member) {
        final List<MemberReservation> memberReservation = memberReservationRepository.findByMemberId(member.getId());
        return memberReservation.stream()
                .map(this::add)
                .toList();
    }

    private MyReservationInfo add(MemberReservation memberReservation) {

        PaymentInfo paymentInfo = paymentRepository.findByRelatedId(memberReservation.getId())
                .map(PaymentInfo::from)
                .orElse(PaymentInfo.NOT_PAYMENT);

        return new MyReservationInfo(
                memberReservation.getId(),
                roomescape.reservation.service.dto.ReservationInfo.from(memberReservation.getReservation()),
                new WaitingResponse(memberReservation.getReservationStatus(), findRankInReservation(memberReservation)),
                paymentInfo);
    }

    private int findRankInReservation(MemberReservation memberReservation) {
        List<MemberReservation> memberReservations = memberReservationRepository.findAllByReservation(
                memberReservation.getReservation());
        return memberReservations.indexOf(memberReservation) + 1;
    }

    @Transactional
    public void updateStatus(ReservationInfo reservation) {
        memberReservationRepository.findFirstByReservationOrderByCreatedAt(reservation)
                .ifPresent(
                        MemberReservation::notPaid
                );
    }

    @Transactional
    public void delete(long reservationId) {
        memberReservationRepository.deleteById(reservationId);
    }
}
