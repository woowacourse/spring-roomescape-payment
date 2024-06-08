package roomescape.service.member;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.dto.ReservationWithRank;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.exception.InvalidMemberException;
import roomescape.service.member.dto.MemberReservationResponse;
import roomescape.service.member.dto.MemberResponse;
import roomescape.service.member.dto.PaymentResponse;
import roomescape.service.member.dto.ReservationStatusResponse;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public MemberService(MemberRepository memberRepository, ReservationRepository reservationRepository, PaymentRepository paymentRepository) {
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream()
            .map(MemberResponse::new)
            .toList();
    }

    public Member findById(long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new InvalidMemberException("존재하지 않는 회원입니다."));
    }

    public List<MemberReservationResponse> findReservations(long memberId) {
        List<ReservationWithRank> reservationWithRanks = reservationRepository.findWithRankingByMemberId(memberId);

        return reservationWithRanks.stream()
            .map(this::createMemberReservationResponse)
            .toList();
    }

    private MemberReservationResponse createMemberReservationResponse(ReservationWithRank reservationWithRank) {
        Optional<Payment> optionalPayment = paymentRepository.findByReservationId(reservationWithRank.getReservation().getId());

        return new MemberReservationResponse(
            reservationWithRank.getReservation().getId(),
            reservationWithRank.getReservation().getTheme().getName().getValue(),
            reservationWithRank.getReservation().getDate(),
            reservationWithRank.getReservation().getTime(),
            new ReservationStatusResponse(reservationWithRank.getReservation().getStatus().getDescription(), reservationWithRank.getRank()),
            optionalPayment.map(PaymentResponse::from).orElse(null)
        );
    }
}
