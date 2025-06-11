package roomescape.theme;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
    @Query(value = """
                    SELECT t
                    FROM Theme t INNER JOIN Reservation r ON t.id = r.schedule.theme.id
                    WHERE r.schedule.date BETWEEN :from AND :to
                    GROUP BY t.id, t.name, t.description, t.thumbnail
                    ORDER BY COUNT(*) DESC
                    LIMIT :size
            """)
    List<Theme> findAllOrderByRank(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("size") int size);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select t from Theme t where t.id = :id")
    Optional<Theme> findByIdForUpdate(Long id);
}
