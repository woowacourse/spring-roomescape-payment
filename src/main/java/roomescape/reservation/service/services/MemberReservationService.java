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
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.repository.MemberReservationRepository;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.service.dto.MyReservationInfo;
import roomescape.reservation.service.dto.PaymentInfo;
import roomescape.reservation.service.dto.ReservationInfo;

@Service
@Transactional(readOnly = true)
public class MemberReservationService {

    private final ReservationRepository reservationRepository;

    private final MemberReservationRepository memberReservationRepository;

    private final PaymentRepository paymentRepository;

    public MemberReservationService(ReservationRepository reservationRepository,
                                    MemberReservationRepository memberReservationRepository, final PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
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

        PaymentInfo paymentInfo = paymentRepository.findByMemberReservationId(memberReservation.getId())
                .map(PaymentInfo::from)
                .orElse(PaymentInfo.NOT_PAYMENT);
        return new MyReservationInfo(
                memberReservation.getId(),
                ReservationInfo.from(memberReservation.getReservation()),
                new WaitingResponse(memberReservation.getReservationStatus(), findRankInReservation(memberReservation)),
                paymentInfo);
    }

    private int findRankInReservation(MemberReservation memberReservation) {
        List<MemberReservation> memberReservations = memberReservationRepository.findAllByReservationId(
                memberReservation.getReservation()
                        .getId());
        return memberReservations.indexOf(memberReservation) + 1;
    }

    @Transactional
    public void updateStatus(Reservation reservation) {
        memberReservationRepository.findFirstByReservationOrderByCreatedAt(reservation)
                .ifPresent(
                        MemberReservation::notPaid
                );
    }

    @Transactional
    public void delete(long reservationId) {
        memberReservationRepository.deleteByReservationId(reservationId);
        reservationRepository.deleteById(reservationId);
    }
}
