package roomescape.domain.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import roomescape.domain.member.entity.Member;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.time.entity.ReservationTime;

public interface ReservationRepositoryInterface {
    Reservation save(final Reservation reservation);

    void deleteById(final Long id);

    boolean existsByDateAndTimeAndTheme(
            final LocalDate date,
            final ReservationTime time,
            final Theme theme);

    List<Reservation> findAll();

    List<Theme> findPopularThemesByReservationBetween(
            final LocalDate dateFrom,
            final LocalDate dateTo,
            final PageRequest pageRequest);

    List<Reservation> findByMember(final Member member);

    Reservation findById(final Long id);

    List<Reservation> findByThemeAndMemberAndDateBetween(
            final Theme theme,
            final Member member,
            final LocalDate dateFrom,
            final LocalDate dateTo);
}

