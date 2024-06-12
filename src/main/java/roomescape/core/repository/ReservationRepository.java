package roomescape.core.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.core.domain.Member;
import roomescape.core.domain.Reservation;
import roomescape.core.domain.ReservationStatus;
import roomescape.core.domain.ReservationTime;
import roomescape.core.domain.Theme;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findAllByDateAndThemeAndStatus(final LocalDate date, final Theme theme,
                                                     final ReservationStatus status);

    List<Reservation> findAllByMemberAndThemeAndDateBetween(final Member member,
                                                            final Theme theme,
                                                            final LocalDate dateFrom,
                                                            final LocalDate dateTo);

    List<Reservation> findAllByMemberAndStatus(final Member member, final ReservationStatus status);

    Integer countByTime(final ReservationTime reservationTime);

    Integer countByTheme(final Theme theme);

    Integer countByMemberAndDateAndTimeAndTheme(final Member member, final LocalDate date, final ReservationTime time,
                                                final Theme theme);

    Integer countByDateAndTimeAndThemeAndStatus(final LocalDate date, final ReservationTime reservationTime,
                                                final Theme theme, final ReservationStatus status);
}
