package roomescape.domain.reservationitem;

import java.time.LocalDate;
import java.util.Optional;

public interface ReservationItemRepository {

    ReservationItem save(ReservationItem reservationItem);

    void delete(ReservationItem reservationItem);

    Optional<ReservationItem> findReservationItemByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    boolean existsByDateAndTimeAndTheme(LocalDate date, Long timeId, Long theme);

    void deleteAllReservationItemByThemeId(long themeId);

    void deleteAllReservationItemByTimeId(long timeId);
}
