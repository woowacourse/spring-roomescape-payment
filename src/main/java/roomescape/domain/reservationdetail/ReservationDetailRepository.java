package roomescape.domain.reservationdetail;

import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;

import java.util.List;
import java.util.Optional;

public interface ReservationDetailRepository {

    Optional<ReservationDetail> findByScheduleAndTheme(Schedule schedule, Theme theme);

    List<ReservationDetail> findByScheduleDateAndThemeId(ReservationDate date, long themeId);

    ReservationDetail save(ReservationDetail reservationDetail);
}
