package roomescape.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import roomescape.domain.ReservationStatus;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByThemeAndDate(Theme theme, LocalDate date);

    List<Reservation> findByThemeIdAndMemberIdAndStatusAndDateBetween(Long themeId, Long memberId, ReservationStatus status, LocalDate dateFrom, LocalDate dateTo);

    @Query("""
            SELECT r.theme, COUNT(r) AS themeCount 
            FROM Reservation r 
            WHERE r.date BETWEEN :startDate AND :endDate 
            GROUP BY r.theme 
            ORDER BY themeCount DESC 
            LIMIT :limit""")
    List<Theme> findAndOrderByPopularity(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("limit") int limit);

    boolean existsByTimeId(long timeId);

    boolean existsByThemeId(long themeId);

    void deleteByMemberIdAndId(long memberId, long reservationId);

    @Query("""
            SELECT COUNT(r) FROM Reservation AS r 
            WHERE r.date = :date 
            AND r.time = :time 
            AND r.theme = :theme 
            AND r.createdAt < :createdAt""")
    long findAndCountWaitingNumber(
            @Param("date") LocalDate date,
            @Param("time") ReservationTime time,
            @Param("theme") Theme theme,
            @Param("createdAt") LocalDateTime localDateTime);

    List<Reservation> findAllByStatus(ReservationStatus status);

    List<Reservation> findAllByStatusNot(ReservationStatus status);

    List<Reservation> findAllByMemberIdAndStatus(long memberId, ReservationStatus status);

    List<Reservation> findAllByMemberIdAndStatusNot(long memberId, ReservationStatus status);

    boolean existsByDateAndTimeIdAndThemeId(LocalDate date, long timeId, long themeId);

    boolean existsByMemberIdAndDateAndTimeIdAndThemeId(long memberId, LocalDate date, long timeId, long themeId);
}
