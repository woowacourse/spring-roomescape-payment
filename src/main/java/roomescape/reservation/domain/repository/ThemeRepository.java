package roomescape.reservation.domain.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.reservation.domain.Theme;

public interface ThemeRepository extends JpaRepository<Theme, Long> {

    @Query("""
                SELECT t
                FROM Theme t
                JOIN MemberReservation mr ON mr.reservation.theme.id = t.id 
                WHERE mr.reservation.date BETWEEN :startDate AND :endDate
                GROUP BY t.id, t.name
                ORDER BY COUNT(*) DESC
            """)
    List<Theme> findTopThemesByMemberReservations(LocalDate startDate, LocalDate endDate, Pageable pageable);
}

