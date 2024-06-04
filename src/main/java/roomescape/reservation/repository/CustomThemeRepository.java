package roomescape.reservation.repository;

import java.util.List;
import roomescape.reservation.model.ReservationDate;
import roomescape.reservation.model.Theme;

public interface CustomThemeRepository {

    List<Theme> findPopularThemes(ReservationDate startAt,
                                  ReservationDate endAt,
                                  int maximumThemeCount);
}
