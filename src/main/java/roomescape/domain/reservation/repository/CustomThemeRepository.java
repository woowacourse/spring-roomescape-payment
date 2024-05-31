package roomescape.domain.reservation.repository;

import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.Theme;

import java.util.List;

public interface CustomThemeRepository {

    List<Theme> findPopularThemes(final ReservationDate startAt, final ReservationDate endAt, final int maximumThemeCount);
}
