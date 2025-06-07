package roomescape.reservation.domain.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByDateAndThemeIdAndStatus(LocalDate date, Long themeId, ReservationStatus status);

    List<Reservation> findByThemeIdAndMemberIdAndDateBetweenAndStatus(Long themeId, Long memberId,
        LocalDate start, LocalDate end, ReservationStatus status);

    boolean existsByTimeIdAndStatus(Long timeId, ReservationStatus status);

    boolean existsByThemeIdAndStatus(Long themeId, ReservationStatus status);

    boolean existsByMemberIdAndThemeIdAndTimeIdAndDateAndStatus(Long memberId, Long themeId,
        Long reservationTimeId, LocalDate date, ReservationStatus status);

    List<Reservation> findByMemberId(Long memberId);
}
