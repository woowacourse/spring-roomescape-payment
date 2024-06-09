package roomescape.reservation.domain.service;

import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.ResourceNotFoundException;
import roomescape.reservation.domain.dto.WaitingReservationRanking;
import roomescape.reservation.domain.entity.MemberReservation;
import roomescape.reservation.domain.entity.Reservation;
import roomescape.reservation.domain.entity.ReservationStatus;
import roomescape.reservation.dto.ReservationSearchRequestParameter;
import roomescape.reservation.repository.MemberReservationRepository;
import roomescape.reservation.repository.ReservationRepository;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberReservationRepository memberReservationRepository;

    public ReservationService(ReservationRepository reservationRepository, MemberReservationRepository memberReservationRepository) {
        this.reservationRepository = reservationRepository;
        this.memberReservationRepository = memberReservationRepository;
    }

    private MemberReservation getMemberReservationById(Long id) {
        return memberReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 예약입니다."));
    }

    public List<MemberReservation> readReservations() {
        return memberReservationRepository.findByStatuses(ReservationStatus.getConfirmationStatuses());
    }

    public List<MemberReservation> readConfirmationMemberReservation(LoginMember loginMember) {
        return memberReservationRepository.findByMemberIdAndStatuses(
                loginMember.id(),
                ReservationStatus.getConfirmationStatuses()
        );
    }

    public List<WaitingReservationRanking> readWaitingMemberReservation(LoginMember loginMember) {
        return memberReservationRepository.findWaitingReservationRankingByMemberId(loginMember.id());
    }

    public List<MemberReservation> searchReservations(ReservationSearchRequestParameter searchCondition) {
        List<Reservation> reservations = reservationRepository.findByDateBetweenAndThemeId(
                searchCondition.dateFrom(),
                searchCondition.dateTo(),
                searchCondition.themeId()
        );

        return memberReservationRepository.findByMemberIdAndReservationIn(searchCondition.memberId(), reservations);
    }

    public MemberReservation readReservation(Long id) {
        return getMemberReservationById(id);
    }

    public void deleteReservation(MemberReservation memberReservation, LoginMember loginMember) {
        memberReservation.validateIsOwner(loginMember);
        deleteReservation(memberReservation);
    }

    public void deleteReservation(MemberReservation memberReservation) {
        memberReservation.validateIsBeforeNow();
        memberReservationRepository.delete(memberReservation);

        confirmFirstWaitingReservation(memberReservation.getReservation());
    }

    private void confirmFirstWaitingReservation(Reservation reservation) {
        memberReservationRepository.findFirstByReservationAndStatus(reservation, ReservationStatus.WAITING)
                .ifPresent((memberReservation) -> memberReservation.setStatus(ReservationStatus.PENDING));
    }

    public void confirmPendingReservation(Long id) {
        MemberReservation memberReservation = getMemberReservationById(id);
        memberReservation.validatePendingStatus();
        memberReservation.setStatus(ReservationStatus.CONFIRMATION);
    }
}
