package roomescape.domain.reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.exception.RoomEscapeException;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    default Reservation getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new RoomEscapeException("존재하지 않는 예약입니다."));
    }

    Optional<Reservation> findById(Long id);

    List<Reservation> findAll();

    List<Reservation> findAllByStatus(Status status);

    List<Reservation> findByPeriodAndThemeAndMember(LocalDate start, LocalDate end, Long memberId, Long themeId);

    List<ReservationWithRank> findWithRank(Long memberId);

    Optional<Reservation> findNextWaiting(ReservationDetail detail);

    boolean existsByDetailAndMemberAndStatusIn(ReservationDetail detail, Member member, List<Status> status);

    boolean existsByDetailAndStatusIn(ReservationDetail detail, List<Status> status);
}
