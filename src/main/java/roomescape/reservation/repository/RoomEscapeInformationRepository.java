package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.domain.RoomEscapeInformation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

public interface RoomEscapeInformationRepository extends JpaRepository<RoomEscapeInformation, Long> {

    boolean existsByThemeId(final Long themeId);

    boolean existsByTimeId(final Long timeId);

    Optional<RoomEscapeInformation> findByDateAndTimeIdAndThemeId(final LocalDate date, final Long timeId, final Long themeId);

    Optional<RoomEscapeInformation> findByDateAndTimeAndTheme(final LocalDate date, final ReservationTime time, final Theme theme);
}
