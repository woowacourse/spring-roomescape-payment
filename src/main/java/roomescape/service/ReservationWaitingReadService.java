package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.response.MemberReservationResponse;
import roomescape.exception.NotFoundException;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationWithPaymentInfo;
import roomescape.model.WaitingWithRank;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ReservationWaitingReadService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;

    public ReservationWaitingReadService(ReservationRepository reservationRepository,
                                         WaitingRepository waitingRepository,
                                         MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.memberRepository = memberRepository;
    }

    public List<MemberReservationResponse> getAllMemberReservationsAndWaiting(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new NotFoundException("해당 id:[%s] 값으로 예약된 내역이 존재하지 않습니다.".formatted(memberId)));

        List<ReservationWithPaymentInfo> memberReservationsWithPaymentInfo = reservationRepository.findAllByMemberWithPaymentInfo(member);
        List<Reservation> memberReservationsWithoutPaymentInfo = reservationRepository.findAllByMemberWithoutPaymentInfo(member);
        List<WaitingWithRank> waitingWithRanks = waitingRepository.findWaitingWithRankByMemberId(memberId);

        List<MemberReservationResponse> allMemberReservations =
                new java.util.ArrayList<>(memberReservationsWithPaymentInfo.stream()
                        .map(MemberReservationResponse::new)
                        .toList());
        List<MemberReservationResponse> allMemberReservationsWithoutPaymentInfo = memberReservationsWithoutPaymentInfo.stream()
                .map(MemberReservationResponse::new)
                .toList();
        List<MemberReservationResponse> waiting = waitingWithRanks.stream()
                .map(MemberReservationResponse::new)
                .toList();

        allMemberReservations.addAll(waiting);
        allMemberReservations.addAll(allMemberReservationsWithoutPaymentInfo);
        return allMemberReservations;
    }
}
