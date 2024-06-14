package roomescape.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    String test = "SELECT r.theme FROM Reservation";

    List<Reservation> findAll();

    Reservation save(final Reservation reservation);

    @Query("""
            SELECT r FROM Reservation r
            WHERE (:theme IS NULL OR r.theme = :theme)
            AND (:member IS NULL OR r.member = :member)
            AND (:dateFrom IS NULL OR r.date >= :dateFrom)
            AND (:dateTo IS NULL OR r.date <= :dateTo)
            """)
    List<Reservation> findByConditions(final Theme theme, final Member member, final LocalDate dateFrom,
                                       final LocalDate dateTo);

    void deleteById(final Long id);

    boolean existsById(final Long id);

    boolean existsByTime(final ReservationTime time);

    boolean existsByDateAndTimeAndTheme(final LocalDate date, final ReservationTime time, final Theme theme);

    List<Reservation> findAllByDateAndTheme(final LocalDate date, final Theme theme);

    List<Reservation> findAllByMember(final Member member);

    boolean existsReservationByThemeAndDateAndTimeAndMember(final Theme theme, final LocalDate date, final ReservationTime time, final Member member);

}
