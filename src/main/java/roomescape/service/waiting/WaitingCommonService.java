package roomescape.service.waiting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InvalidMemberException;
import roomescape.exception.InvalidReservationException;
import roomescape.service.reservation.dto.ReservationResponse;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class WaitingCommonService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    public WaitingCommonService(ReservationRepository reservationRepository, MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAllByStatus(ReservationStatus.WAITING).stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional
    public void deleteWaitingById(long reservationId, long memberId) {
        Member member = getById(memberId);
        reservationRepository.findById(reservationId)
                .ifPresent(reservation -> cancelWaiting(member, reservation));
    }

    private Member getById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidMemberException("회원 정보를 찾을 수 없습니다."));
    }

    private void cancelWaiting(Member member, Reservation reservation) {
        validateAuthority(reservation, member);
        validateStatus(reservation);
        reservationRepository.deleteById(reservation.getId());
        updateToPendingPayment(reservation);
    }

    private void validateAuthority(Reservation reservation, Member member) {
        if (member.isGuest() && !reservation.isReservationOf(member)) {
            throw new ForbiddenException("예약 대기를 삭제할 권한이 없습니다.");
        }
    }

    private void validateStatus(Reservation reservation) {
        if (reservation.isReserved()) {
            throw new InvalidReservationException("예약은 삭제할 수 없습니다. 관리자에게 문의해주세요.");
        }
    }

    private void updateToPendingPayment(Reservation reservation) {
        if (reservation.isPendingPayment()) {
            ReservationDetail detail = reservation.getDetail();
            reservationRepository.findFirstByDetailIdOrderByCreatedAt(detail.getId())
                    .ifPresent(Reservation::pendingPayment);
        }
    }
}
