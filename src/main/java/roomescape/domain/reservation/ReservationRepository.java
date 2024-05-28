package roomescape.domain.reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    boolean existsByInfo(ReservationInfo info);

    boolean existsByInfoTime(ReservationTime time);

    boolean existsByInfoTheme(Theme theme);

    List<Reservation> findByMemberId(Long id);

    Optional<Reservation> findByInfo(ReservationInfo info);

    @Query("""
            SELECT r.info.time.id
            FROM Reservation r
            WHERE r.info.date = :date AND r.info.theme.id = :themeId
            """)
    List<Long> findTimeIdByDateAndThemeId(LocalDate date, long themeId);

    @Query("""
            SELECT t
            FROM Reservation r
            LEFT JOIN Theme t ON t.id=r.info.theme.id
            WHERE r.info.date > :startDate AND r.info.date < :endDate
            GROUP BY t.id
            ORDER BY COUNT(*) DESC
            LIMIT 10
            """)
    List<Theme> findThemeByMostPopularReservation(LocalDate startDate, LocalDate endDate);

    interface Specs {
        static Specification<Reservation> hasMemberId(Long memberId) {
            return (root, query, builder) -> {
                if (memberId == null) {
                    return builder.conjunction();
                }
                return builder.equal(root.get("member").get("id"), memberId);
            };
        }

        static Specification<Reservation> hasThemeId(Long themeId) {
            return (root, query, builder) -> {
                if (themeId == null) {
                    return builder.conjunction();
                }
                return builder.equal(root.get("info").get("theme").get("id"), themeId);
            };
        }

        static Specification<Reservation> hasStartDate(LocalDate dateFrom) {
            return (root, query, builder) -> {
                if (dateFrom == null) {
                    return builder.conjunction();
                }
                return builder.greaterThanOrEqualTo(root.get("info").get("date"), dateFrom);
            };
        }

        static Specification<Reservation> hasEndDate(LocalDate dateTo) {
            return (root, query, builder) -> {
                if (dateTo == null) {
                    return builder.conjunction();
                }
                return builder.lessThanOrEqualTo(root.get("info").get("date"), dateTo);
            };
        }
    }
}
