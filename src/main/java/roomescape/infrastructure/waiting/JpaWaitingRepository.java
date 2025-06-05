package roomescape.infrastructure.waiting;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import roomescape.domain.member.entity.Member;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.time.entity.ReservationTime;
import roomescape.domain.waiting.entity.Waiting;

public interface JpaWaitingRepository extends CrudRepository<Waiting, Long> {

    @Query("""
                SELECT w
                FROM Waiting w
                JOIN FETCH w.time
                JOIN FETCH w.theme
                WHERE w.member = :member
            """)
    List<Waiting> findByMember(final Member member);

    boolean existsByDateAndTimeAndTheme(final LocalDate date, final ReservationTime reservationTime, final Theme theme);

    @Query("""
            SELECT COUNT(w)
            FROM Waiting w
            WHERE w.theme = :theme
              AND w.date = :date
              AND w.time = :time
              AND w.id < :id
            """)
    long countBefore(final Theme theme, final LocalDate date, final ReservationTime time, final Long id);

    @Query("""
            SELECT w
            FROM Waiting w
            JOIN FETCH w.time
            JOIN FETCH w.theme
            JOIN FETCH w.member
            """)
    List<Waiting> findAll();

    Optional<Waiting> findFirstByThemeAndDateAndTimeOrderByIdAsc(
            final Theme theme,
            final LocalDate date,
            final ReservationTime time
    );
}
