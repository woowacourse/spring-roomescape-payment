package roomescape.domain.reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;

@Repository
public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Reservation getReservation(Long id);

    Optional<Reservation> findReservation(Long id);

    List<Reservation> findAll();

    List<Reservation> findAll(Status status);

    List<Reservation> findReservation(LocalDate start, LocalDate end, Long memberId, Long themeId);

    List<ReservationWithRank> findWithRank(Long memberId);

    Optional<Reservation> findNextWaiting(Theme theme, LocalDate date, ReservationTime time);

    boolean existsReservation(Theme theme, LocalDate date, ReservationTime time, List<Status> status);

    boolean existsReservation(Theme theme, LocalDate date, ReservationTime time, Member member, List<Status> status);
}
