package roomescape.reservation.service.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.reservation.controller.dto.ReservationQueryRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.controller.dto.WaitingResponse;
import roomescape.reservation.domain.MemberReservation;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.repository.MemberReservationRepository;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.service.dto.MyReservationInfo;

@Service
@Transactional(readOnly = true)
public class MemberReservationService {

    private final ReservationRepository reservationRepository;

    private final MemberReservationRepository memberReservationRepository;

    public MemberReservationService(ReservationRepository reservationRepository,
                                    MemberReservationRepository memberReservationRepository) {
        this.reservationRepository = reservationRepository;
        this.memberReservationRepository = memberReservationRepository;
    }


    public List<ReservationResponse> findMemberReservations(ReservationQueryRequest request) {
        return memberReservationRepository.findBy(
                        request.getMemberId(),
                        request.getThemeId(),
                        request.getStartDate(),
                        request.getEndDate())
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<MyReservationInfo> findMyReservations(Member member) {
        final List<MemberReservation> memberReservation = memberReservationRepository.findByMemberId(member.getId());
        return memberReservation.stream().map(this::add).toList();
    }

    public MyReservationInfo add(MemberReservation memberReservation) {
        List<MemberReservation> memberReservations = memberReservationRepository.findAllByReservationId(
                memberReservation.getReservation().getId());
        int rank = memberReservations.indexOf(memberReservation) + 1;
        return new MyReservationInfo(
                memberReservation.getId(),
                memberReservation.getReservation().getThemeName(),
                memberReservation.getReservation().getDate(),
                memberReservation.getReservation().getTimeValue(),
                new WaitingResponse(memberReservation.getReservationStatus(), rank),
                memberReservation.getPrice());
    }

    @Transactional
    public void updateStatus(Reservation reservation) {
        memberReservationRepository.findFirstByReservationOrderByCreatedAt(reservation)
                .ifPresent(MemberReservation::notPaid);
    }

    @Transactional
    public void delete(long reservationId) {
        memberReservationRepository.deleteByReservationId(reservationId);
        reservationRepository.deleteById(reservationId);
    }
}
