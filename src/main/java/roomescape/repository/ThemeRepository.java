package roomescape.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import roomescape.model.Theme;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ThemeRepository extends CrudRepository<Theme, Long> {

    List<Theme> findAll();

    Theme save(final Theme theme); // save

    void deleteById(final Long id); // delete

    Optional<Theme> findById(final Long id);

    @Query("""
            SELECT r.theme
            FROM Reservation r
            INNER JOIN Theme th on r.theme.id = th.id
            WHERE r.date BETWEEN :before AND :after
            GROUP BY th
            ORDER BY COUNT(th) DESC
            limit 10
            """)
    List<Theme> findFirst10ByDateBetweenOrderByTheme(@Param("before") final LocalDate before,
                                                     @Param("after") final LocalDate after);
}
