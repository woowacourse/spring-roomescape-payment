package roomescape.domain.reservationdetail;

import java.util.List;
import java.util.Optional;
import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;

public interface ReservationDetailRepository {

    Optional<ReservationDetail> findByScheduleAndTheme(Schedule schedule, Theme theme);

    List<ReservationDetail> findByScheduleDateAndThemeId(ReservationDate date, long themeId);

    ReservationDetail save(ReservationDetail reservationDetail);
}
