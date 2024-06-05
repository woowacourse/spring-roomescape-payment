package roomescape.fixture;

import java.time.LocalDate;
import java.util.List;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;

public class ReservationDetailFixture {
    public static List<ReservationDetail> createReservationDetails(
            List<Theme> themes,
            LocalDate date,
            ReservationTime time
    ) {
        return themes.stream()
                .map(theme -> createReservationDetail(date, time, theme))
                .toList();
    }

    public static ReservationDetail createReservationDetail(LocalDate date, ReservationTime time, Theme theme) {
        return new ReservationDetail(date, time, theme);
    }
}
