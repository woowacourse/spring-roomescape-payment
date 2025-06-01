package roomescape.repository.querydsl;

import roomescape.domain.reservation.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepositoryCustom {

    List<Reservation> findByMemberIdAndThemeIdAndDateFromAndDateTo(Long memberId,
                                                                   Long themeId,
                                                                   LocalDate dateFrom,
                                                                   LocalDate dateTo);
}
