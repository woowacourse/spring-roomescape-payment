package roomescape.domain.reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationDetail;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Reservation getReservation(Long id);

    Optional<Reservation> findReservation(Long id);

    List<Reservation> findAll();

    List<Reservation> findAll(Status status);

    List<Reservation> findReservation(LocalDate start, LocalDate end, Long memberId, Long themeId);

    List<ReservationWithRank> findWithRank(Long memberId);

    Optional<Reservation> findNextWaiting(ReservationDetail detail);

    boolean existsReservation(ReservationDetail detail, List<Status> status);

    boolean existsReservation(ReservationDetail detail, Member member, List<Status> status);
}
