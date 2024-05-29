package roomescape.infrastructure.reservationdetail;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;

public interface ReservationDetailJpaRepository extends JpaRepository<ReservationDetail, Long>, ReservationDetailRepository {

    Optional<ReservationDetail> findByScheduleAndTheme(Schedule schedule, Theme theme);

    List<ReservationDetail> findByScheduleDateAndThemeId(ReservationDate date, long themeId);
}
